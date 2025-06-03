package server;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import server.database.DatabaseManager;
import server.database.UserBuilder;
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

            if (true) {
                System.out.println("[DEBUG][sendMessage] iv  " + Base64.getEncoder().encodeToString(em.iv));
                System.out.println("[DEBUG][sendMessage] ct  " + Base64.getEncoder().encodeToString(em.ciphertext));
                System.out.println("[DEBUG][sendMessage] pt  " + Base64.getEncoder().encodeToString(plaintext));
                System.out.println("[DEBUG][sendMessage] obj " + obj.getClass());
            }

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

        if (true) {
            System.out.println("[DEBUG][Handshake] AES key (Base64): " + Base64.getEncoder().encodeToString(aesBytes));
        }

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

        // salt = handleLower
        byte[] salt = req.handle.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] hashedKey = CryptoUtil.kdf(req.authKey, salt);

        User newUser = new UserBuilder()
                .setHandle(req.handle)
                .setNickname(req.nickname)
                .setPublicKey(req.clientPublicKey)
                .setEncryptedPrivateKey(req.encClientPrivKey)
                .setEncryptedPrivateKeyIv(Arrays.copyOf(req.encClientPrivKey, 12))
                .setAuthenticationKey(req.authKey)
                .createUser();

        populatePrivateRooms(newUser);
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

    private void populatePrivateRooms(User newUser) throws Exception {

        DatabaseManager db = DatabaseManager.getInstance();

        // create 1 room for each existing user
        for (User peer : User.queryAll()) {
            if (peer.getId() == newUser.getId()) continue;

            // already exists with this peer?
            boolean exists = db.query("""
            SELECT 1
            FROM rooms r
            JOIN room_users ru1 ON ru1.room_id = r.id
            JOIN room_users ru2 ON ru2.room_id = r.id
            WHERE ru1.user_id = ? AND ru2.user_id = ?
            GROUP BY r.id
            HAVING COUNT(*) = 2
        """, s -> {
                s.setLong(1, newUser.getId());
                s.setLong(2, peer.getId());
                try (var rs = s.executeQuery()) { return rs.next(); }
            });
            if (exists) continue;

            long roomId  = db.generateId();
            db.query("INSERT INTO rooms(id,name) VALUES(?,?)",
                    s->{ s.setLong(1, roomId); s.setString(2, ""); return s.executeUpdate(); });

            byte[] roomKey    = CryptoUtil.generateRandomBytes(32);
            byte[] encForNew  = CryptoUtil.encryptRSA(roomKey, CryptoUtil.bytesToPub(newUser.getPublicKey()));
            byte[] encForPeer = CryptoUtil.encryptRSA(roomKey, CryptoUtil.bytesToPub(peer.getPublicKey()));

            db.query("INSERT INTO room_users VALUES(?,?,?)",
                    s->{ s.setLong(1, roomId); s.setLong(2, newUser.getId());
                        s.setBytes(3, encForNew);  return s.executeUpdate(); });
            db.query("INSERT INTO room_users VALUES(?,?,?)",
                    s->{ s.setLong(1, roomId); s.setLong(2, peer.getId());
                        s.setBytes(3, encForPeer); return s.executeUpdate(); });

            Connection dst = online.get(peer.getId());
            if (dst != null) {
                CreateRoom cr = new CreateRoom();
                cr.roomId        = roomId;
                cr.roomName      = "";
                cr.memberHandles = new String[]{ newUser.getHandle(), peer.getHandle() };
                cr.encKeyForMe   = encForPeer;
                sendMessage(cr, dst);
            }
        }

        // self room
        long selfRoomId = db.generateId();
        db.query("INSERT INTO rooms(id,name) VALUES(?,?)",
                s -> { s.setLong(1, selfRoomId); s.setString(2, ""); return s.executeUpdate(); });

        byte[] selfKey   = CryptoUtil.generateRandomBytes(32);
        byte[] encForMe  = CryptoUtil.encryptRSA(selfKey, CryptoUtil.bytesToPub(newUser.getPublicKey()));

        db.query("INSERT INTO room_users VALUES(?,?,?)",
                s -> { s.setLong(1, selfRoomId); s.setLong(2, newUser.getId());
                    s.setBytes(3, encForMe); return s.executeUpdate(); });

    }


    private void handleLogin(LoginRequest req, Connection c) throws Exception {

        User u = User.queryByHandle(req.handle);
        LoginResponse lr = new LoginResponse();

        if (u == null) {
            lr.success = false;
            sendMessage(lr, c);
            return;
        }

        byte[] salt = req.handle.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] candidate = CryptoUtil.kdf(req.authKey, salt);

        if (!CryptoUtil.secureCompareBytes(candidate, u.getHashedAuthenticationKey())) {
            lr.success = false;
            sendMessage(lr, c);
            return;
        }

        // correct authKey ⇒ send challenge
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
        lr.userInfo = ok ? new UserInfo(u.getId(),u.getHandle(), u.getNickname(), u.getBio(),
                null, u.getPublicKey())
                : null;

        if (ok)
        {
            SessionManager.removePendingChallenge(handle);
            online.put(u.getId(), c);
        }

        sendMessage(lr, c);
    }

    private void handleRequestCreateRoom(RequestCreateRoom req, Connection c) throws Exception {

        long roomId = DatabaseManager.getInstance().generateId();
        String name = req.roomName.trim();

        var db = DatabaseManager.getInstance();

        db.query("INSERT INTO rooms(id,name) VALUES(?,?)",
                s->{ s.setLong(1, roomId); s.setString(2, name); return s.executeUpdate(); });

        byte[] key = CryptoUtil.generateRandomBytes(32); // 256-bit

        for (String h : req.memberHandles) {
            User u = User.queryByHandle(h);
            byte[] enc = CryptoUtil.encryptRSA(key, CryptoUtil.bytesToPub(u.getPublicKey()));

            db.query("INSERT INTO room_users VALUES(?,?,?)",
                    s->{ s.setLong(1, roomId); s.setLong(2, u.getId()); s.setBytes(3, enc); return s.executeUpdate(); });

            // notify each member
            CreateRoom cr = new CreateRoom();
            cr.roomId        = roomId;
            cr.roomName      = name;
            cr.memberHandles = req.memberHandles;
            cr.encKeyForMe   = enc;
            sendMessageToUser(u, cr);
        }
    }

    // helper
    private void sendMessageToUser(User u, NetworkedMessage msg){
        Connection dst = online.get(u.getId());
        if (dst != null) sendMessage(msg, dst);
    }

    private void handleRequestRoomList(Connection c) {

        SessionInfo sess = sessionTable.get(connectionTable.get(c));
        User        me   = User.queryByHandle(sess.getHandleLower());
        var         db   = DatabaseManager.getInstance();

        RoomDTO[] list = db.query("""
        SELECT r.id, r.name, ru.encrypted_key
          FROM rooms r
          JOIN room_users ru ON ru.room_id = r.id
         WHERE ru.user_id = ?
    """, stRooms -> {
            stRooms.setLong(1, me.getId());

            List<RoomDTO> rooms = new ArrayList<>();

            try (var rsRooms = stRooms.executeQuery()) {
                while (rsRooms.next()) {

                    RoomDTO dto  = new RoomDTO();
                    dto.roomId   = rsRooms.getLong(1);
                    dto.roomName = rsRooms.getString(2);
                    dto.encKey   = rsRooms.getBytes(3);

                    // participants
                    dto.memberHandles = db.query("""
                    SELECT u.handle
                      FROM room_users ru
                      JOIN users u ON u.id = ru.user_id
                     WHERE ru.room_id = ?
                """, stMems -> {
                        stMems.setLong(1, dto.roomId);
                        List<String> hs = new ArrayList<>();
                        try (var rsMems = stMems.executeQuery()) {
                            while (rsMems.next()) hs.add(rsMems.getString(1));
                        }
                        return hs.toArray(new String[0]);
                    });

                    // message history
                    dto.messages = db.query("""
                    SELECT id, author_id, created_at, encrypted_content
                      FROM messages
                     WHERE room_id = ?
                  ORDER BY created_at
                """, stMsg -> {
                        stMsg.setLong(1, dto.roomId);
                        List<MsgDTO> msgs = new ArrayList<>();
                        try (var rsMsg = stMsg.executeQuery()) {
                            while (rsMsg.next()) {
                                MsgDTO m   = new MsgDTO();
                                m.id              = rsMsg.getLong(1);
                                m.authorId        = rsMsg.getLong(2);
                                m.createdAt       = rsMsg.getLong(3);
                                m.encryptedContent= rsMsg.getBytes(4);
                                msgs.add(m);
                            }
                        }
                        return msgs.toArray(new MsgDTO[0]);
                    });

                    rooms.add(dto);
                }
            }
            return rooms.toArray(new RoomDTO[0]);
        });

        RoomList rl = new RoomList();
        rl.rooms = list;
        sendMessage(rl, c);
    }



    private void handleSendMessage(SendMessage req, Connection c) throws Exception {

        SessionInfo sess = sessionTable.get(connectionTable.get(c));
        User author      = User.queryByHandle( sess.getHandleLower() );

        long msgId = DatabaseManager.getInstance().generateId();
        long now   = System.currentTimeMillis();

        var db = DatabaseManager.getInstance();

        db.query("INSERT INTO messages(id, room_id, author_id, created_at, encrypted_content)" +
                " VALUES(?,?,?,?,?)", s -> {
            s.setLong(1, msgId); s.setLong(2, req.roomId); s.setLong(3, author.getId());
            s.setLong(4, now);   s.setBytes(5, req.encryptedContent); return s.executeUpdate();
        });

        ReceivedMessage out = new ReceivedMessage();
        out.roomId           = req.roomId;
        out.createdAt        = now;
        out.authorHandle     = author.getHandle();
        out.encryptedContent = req.encryptedContent;

        // members of the room
        List<Long> memberIds = db.query("SELECT user_id FROM room_users WHERE room_id = ?",
                s->{ s.setLong(1, req.roomId); List<Long> ids=new ArrayList<>();
                    try(var r=s.executeQuery()){while(r.next()) ids.add(r.getLong(1));} return ids;});

        for (Long uid : memberIds){
            Connection dst = online.get(uid);
            if (dst != null) sendMessage(out, dst);
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
