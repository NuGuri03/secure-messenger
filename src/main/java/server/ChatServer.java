package server;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import networked.*;
import networked.messages.*;
import server.database.UserBuilder;
import server.models.Room;
import server.models.RoomUser;
import server.models.User;

public class ChatServer {
    public static final int TCP_PORT = 23456;

    private final Server kryoServer;
    private final PrivateKey serverPrivateKey;

    private final Map<Long, SessionInfo> sessionTable = new ConcurrentHashMap<>();
    private final Map<Connection, Long> connectionTable = new ConcurrentHashMap<>();
    private final Map<Long, Connection> online = new ConcurrentHashMap<>();


    public ChatServer() throws Exception {
        serverPrivateKey = initializeServerKey();
        kryoServer = initializeKryoServer();
    }

    public void start() throws IOException {
        //start server and bind ports
        kryoServer.start();
        kryoServer.bind(TCP_PORT);
        System.out.printf("[ChatServer] Server started on TCP %d%n", TCP_PORT);
    }


    private PrivateKey initializeServerKey() throws Exception {
        String baseDir = System.getProperty("user.dir");
        var privPath = Paths.get(baseDir, "server_private.pem");
        var pubPath = Paths.get(baseDir, "server_public.pem");
        var keyPair = CryptoUtil.loadOrGenerateRSAKeyPair(privPath, pubPath);

        return keyPair.getPrivate();
    }

    private Server initializeKryoServer() {
        var kryoServer = new Server(65536,65536);

        var kryo = kryoServer.getKryo();
        for (var messageType : MessageTypeIndex.getAllMessageTypes()) {
            kryo.register(messageType);
        }

        kryoServer.addListener(new Listener() {
            @Override
            public void connected(Connection c) {
                serverConnected(c);
            }

            @Override
            public void disconnected(Connection c) {
                serverDisconnected(c);
            }

            @Override
            public void received(Connection c, Object obj) {
                try {
                    serverReceived(c, obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return kryoServer;
    }

    private void serverConnected(Connection c) {
        System.out.println("[ChatServer] Client connected: " + c.getRemoteAddressTCP());
    }

    private void serverDisconnected(Connection c) {
        System.out.println("[ChatServer] Client disconnected: " + c.getRemoteAddressTCP());

        // remove from connection table
        Long sessionId = connectionTable.remove(c);
        if (sessionId != null) {
            sessionTable.remove(sessionId);
        }

        online.values().remove(c);
    }

    private void serverReceived(Connection c, Object obj) throws Exception {
        if (!(obj instanceof PreSessionMessage psm)) { return; }

        switch (psm) {
            case KeyExchangeMessage kx -> handleKeyExchangeMessage(c, kx);
            case EncryptedMessage em -> handleEncryptedMessage(c, em);
            default -> System.out.println("[ChatServer] [ERROR] Unknown PreSessionMessage " + psm.getClass());
        }
    }

    private void sendMessage(NetworkedMessage obj, Connection c) {
        Long sessionId = connectionTable.get(c);
        if (sessionId == null) { throw new IllegalStateException("Session ID not found"); }

        SessionInfo session = sessionTable.get(sessionId);
        if (session == null) { throw new IllegalStateException("Session data not found"); }

        obj.seqNumber = session.nextSeqNumber();

        try {
            // serialize with kryo
            byte[] plaintext = serializeMessage(obj);
            
            // encrypt
            EncryptedMessage em = new EncryptedMessage();
            em.iv = CryptoUtil.generateIV();
            em.ciphertext = CryptoUtil.encrypt(plaintext, session.getSecretKey(), em.iv);
            em.sessionId = 0;

            System.out.println("[DEBUG][sendMessage] obj " + obj.getClass());

            c.sendTCP(em);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NetworkedMessage deserializeMessage(byte[] data) {
        try (var input = new Input(data)) {
            var object = kryoServer.getKryo().readClassAndObject(input);

            if (!(object instanceof NetworkedMessage msg)) { return null; }
            return msg;
        }
    }

    private byte[] serializeMessage(NetworkedMessage msg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryoServer.getKryo().writeClassAndObject(output, msg);
        output.close();

        byte[] plaintext = baos.toByteArray();
        return plaintext;
    }

    private void handleKeyExchangeMessage(Connection c, KeyExchangeMessage kx) throws Exception {
        // 1. decrypt handshake using server rsa privatekey
        byte[] aesBytes = CryptoUtil.decryptRSA(kx.encryptedKey, serverPrivateKey);
        SecretKey aes = new SecretKeySpec(aesBytes, "AES");

        // 2. generate session id
        long sessionId = CryptoUtil.generateRandomId();
        connectionTable.put(c, sessionId);
        sessionTable.put(sessionId, new SessionInfo(sessionId, aes));

        // 3. send back SessionHelloMessage
        SessionHelloMessage hello = new SessionHelloMessage();
        hello.sessionId = sessionId;
        sendMessage(hello, c);
    }

    private void handleEncryptedMessage(Connection c, EncryptedMessage em) throws Exception {
        // 1. get session info
        SessionInfo session = sessionTable.get(em.sessionId);
        if (session == null) { return; }

        // 2. decrypt message
        byte[] plaintext = CryptoUtil.decrypt(em.ciphertext, session.getSecretKey(), em.iv);

        // 3. deserialize inner object
        Object message = deserializeMessage(plaintext);
        if (!(message instanceof NetworkedMessage msg)) { return; }

        // 4. check seq num
        if (!session.receiveSeqNumber(msg.seqNumber)) {
            System.out.println("[ChatServer] [ERROR] Received message with wrong sequence number. Discarding!");
            return;
        }

        // 5. dispatch by type
        dispatchNetworkedMessage(msg, c);
    }

    private void dispatchNetworkedMessage(NetworkedMessage msg, Connection c) throws Exception {
        switch (msg) {
            case RegisterRequest rr -> handleRegister(rr, c);
            case LoginRequest lr -> handleLogin(lr, c);
            case LoginChallengeResponse lcr -> handleLoginChallengeResponse(lcr, c);
            case AllUserInfoRequest auir -> handleAllUserInfoRequest(c);
            case RequestCreateRoom rcr -> handleRequestCreateRoom(rcr, c);
            case RequestRoomList rrl -> handleRequestRoomList(c);
            case SendMessage sm  -> handleSendMessage(sm,  c);
            case UserInfoChangeRequest uicr -> handleUserInfoChangeRequest(uicr, c);

            default -> System.out.println("[ChatServer] [WARN] Unhandled NetworkedMessage: " + msg.getClass());
        }
    }

    // --------------------- handle methods --------------------- //

    private void handleRegister(RegisterRequest req, Connection c) throws Exception {

        RegisterResponse resp = new RegisterResponse();

        if (User.isHandleExists(req.handle)) {
            resp.success = false;
            resp.message = "handle already exists";
            sendMessage(resp, c);
            return;
        }

        User newUser = new UserBuilder()
                .setHandle(req.handle)
                .setNickname(req.nickname)
                .setPublicKey(req.clientPublicKey)
                .setEncryptedPrivateKey(req.encClientPrivKey)
                .setEncryptedPrivateKeyIv(Arrays.copyOf(req.encClientPrivKey, 12))
                .setAuthenticationKey(req.authKey)
                .createUser();

        sendCreatedUserNotification(newUser);

        resp.success = true;
        resp.message = "registration successful";
        sendMessage(resp, c);
    }

    private void sendCreatedUserNotification(User newUser) {
        // Notify all online users about the new user
        for (Connection dst : online.values()) {
            UserInfo userInfo = new UserInfo(newUser.getId(), newUser.getHandle(), newUser.getNickname(), newUser.getBio(), null, newUser.getPublicKey());
            NewUserCreated notification = new NewUserCreated(userInfo);
            sendMessage(notification, dst);
        }
    }

    private void handleLogin(LoginRequest req, Connection c) throws Exception {
        User u = User.queryByHandle(req.handle);
        LoginResponse lr = new LoginResponse();

        // User not found?
        if (u == null) {
            lr.success = false;
            sendMessage(lr, c);
            return;
        }

        // Invalid authentication key?
        if (!u.verifyAuthenticationKey(req.authKey)) {
            lr.success = false;
            sendMessage(lr, c);
            return;
        }

        // correct authKey -> send challenge
        Long sessionId   = connectionTable.get(c);
        SessionInfo sess = sessionTable.get(sessionId);
        if (sess != null) {
            sess.setHandleLower(req.handle.toLowerCase());
        }

        byte[] challenge = CryptoUtil.generateRandomBytes(16);
        SessionManager.storePendingChallenge(req.handle.toLowerCase(), challenge);

        LoginChallenge ch = new LoginChallenge();
        ch.clientPublicKey  = u.getPublicKey();
        ch.encClientPrivKey = u.getEncryptedPrivateKey();
        ch.challenge  = challenge;
        sendMessage(ch, c);
    }

    private void handleLoginChallengeResponse(LoginChallengeResponse resp,
                                              Connection c) throws Exception {
        SessionInfo sess = sessionTable.get( connectionTable.get(c) );
        if (sess == null) return;
        String handle = sess.getHandleLower();

        User u = User.queryByHandle(handle);

        byte[] original = SessionManager.getPendingChallenge(handle);
        if (original == null) { return; } // no pending challenge

        PublicKey pub = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(u.getPublicKey()));

        boolean ok = CryptoUtil.verifyWithRsa4096(pub, original, resp.challengeSignature);

        LoginResponse lr = new LoginResponse();
        lr.success  = ok;
        lr.userInfo = null;

        if (ok)
        {
            lr.userInfo = new UserInfo(u.getId(), u.getHandle(), u.getNickname(), u.getBio(), null, u.getPublicKey());
            SessionManager.removePendingChallenge(handle);
            online.put(u.getId(), c);
        }

        sendMessage(lr, c);
    }

    private void handleRequestCreateRoom(RequestCreateRoom req, Connection c) throws Exception {
        SessionInfo sess = sessionTable.get( connectionTable.get(c) );
        if (sess == null) { return; }

        // User is in memberHandles?
        boolean hasSelf = Arrays.stream(req.memberHandles).anyMatch((str) -> str.equalsIgnoreCase(sess.getHandleLower()));
        if (!hasSelf) {
            System.out.println("[ChatServer] [ERROR] User " + sess.getHandleLower() + " not in member list for room creation.");
            return;
        }

        // Array length mismatches?
        if (req.memberHandles.length > req.encryptedKeys.length) {
            System.out.println("[ChatServer] [ERROR] Member handles and encrypted keys length mismatch.");
            return;
        }

        String name = req.roomName.trim();
        var room = Room.create(name);

        for (int i = 0; i < req.memberHandles.length; i++) {
            User u = User.queryByHandle(req.memberHandles[i]);
            byte[] encKey = req.encryptedKeys[i];

            if (u == null) {
                System.out.println("[ChatServer] [WARN] User not found for handle: " + req.memberHandles[i]);
                continue;
            }
            
            room.addUser(u, encKey);

            // notify each member
            CreateRoom cr = new CreateRoom();
            cr.roomId        = room.getId();
            cr.roomName      = room.getName();
            cr.memberHandles = req.memberHandles;
            cr.encKeyForMe   = encKey;
            sendMessageToUser(u, cr);
        }
    }

    private void handleUserInfoChangeRequest(UserInfoChangeRequest req, Connection c) {
        SessionInfo sess = sessionTable.get(connectionTable.get(c));
        if (sess == null) return;

        User u = User.queryByHandle(sess.getHandleLower());
        if (u == null) return;

        // Update user info
        try {
            u.setNickname(req.username);
            u.setBio(req.bio);
        } catch (SQLException e) {
            System.out.println("[ChatServer] [ERROR] Failed to update user info: " + e.getMessage());
            return;
        }
        
        // Notify the user about the change
        UserInfoChanged change = new UserInfoChanged();
        change.handle = u.getHandle();
        change.username = u.getNickname();
        change.bio = u.getBio();

        // Notify all online users about the change
        for (Connection dst : online.values()) {
            sendMessage(change, dst);
        }
    }

    // helper
    private void sendMessageToUser(User u, NetworkedMessage msg){
        Connection dst = online.get(u.getId());
        if (dst != null) sendMessage(msg, dst);
    }

    private void handleRequestRoomList(Connection c) {
        SessionInfo sess = sessionTable.get(connectionTable.get(c));
        User me = User.queryByHandle(sess.getHandleLower());
        var rooms = me.getRooms();

        List<RoomDTO> list = Arrays.stream(rooms)
            .map(roomUser -> {
                RoomDTO dto = new RoomDTO();
                dto.roomId   = roomUser.getRoomId();
                dto.roomName = roomUser.getRoomName();
                dto.encKey   = roomUser.getEncryptedKey();

                // participants
                dto.memberHandles = Arrays.stream(roomUser.getRoom().getUsers())
                    .map(RoomUser::getUser)
                    .map(User::getHandle)
                    .toArray(String[]::new);

                // message history
                dto.messages = Arrays.stream(roomUser.getRoom().getRecentMessages(100, System.currentTimeMillis() + 10000))
                    .map(msg -> {
                        MsgDTO m = new MsgDTO();
                        m.id              = msg.getId();
                        m.authorId        = msg.getAuthorId();
                        m.createdAt       = msg.getCreatedAt();
                        m.encryptedContent= msg.getEncryptedContent();
                        return m;
                    })
                    .toArray(MsgDTO[]::new);

                return dto;
            })
            .toList();

        RoomList rl = new RoomList();
        rl.rooms = list.toArray(RoomDTO[]::new);
        sendMessage(rl, c);
    }

    private void handleSendMessage(SendMessage req, Connection c) throws Exception {
        SessionInfo sess = sessionTable.get(connectionTable.get(c));
        User author      = User.queryByHandle( sess.getHandleLower() );

        Room room = Room.queryById(req.roomId);
        if (room == null) {
            System.out.println("[ChatServer] [ERROR] Room not found: " + req.roomId);
            return;
        }

        var message = room.addMessage(author, req.encryptedContent);

        ReceivedMessage out = new ReceivedMessage();
        out.roomId           = req.roomId;
        out.createdAt        = message.getCreatedAt();
        out.authorHandle     = author.getHandle();
        out.encryptedContent = req.encryptedContent;

        // members of the room
        var members = room.getUsers();

        for (var roomUser : members){
            Connection dst = online.get(roomUser.getUserId());
            if (dst != null) {
                sendMessage(out, dst);
            }
        }
    }

    private void handleAllUserInfoRequest(Connection c) {
        AllUserInfoResponse resp = new AllUserInfoResponse();

        User[] users = User.queryAll();

        UserInfo[] userInfos = new UserInfo[users.length];
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            userInfos[i] = new UserInfo(user.getId(),user.getHandle(), user.getNickname(), user.getBio(),
                                        null, user.getPublicKey());
        }
        resp.setUserInfos(userInfos);

        sendMessage(resp, c);
    }
}
