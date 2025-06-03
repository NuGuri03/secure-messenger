package client;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import networked.*;
import networked.messages.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.function.Consumer;

public class ChatClient {
    private final boolean debug = true;

    private final Client kryoClient;
    private final ServerInfo serverInfo;
    private SecretKey sessionSecret;
    private Connection connection;

    private int clientSeqNumber = 0;
    private int serverSeqNumber = -1;
    private long sessionId = 0;

    private UserInfo currentUser;
    private PrivateKey myPrivateKey;

    //holds the 64-byte masterKey while waiting for LoginChallenge
    private byte[] pendingLoginMasterKey = null;
    //lower-case handle, also kept until the challenge is finished
    private String pendingHandleLower   = null;


   public List<UserInfo> friendList = new ArrayList<>();
   public HashMap<RoomInfo, List<UserInfo>> rooms = new HashMap<>();

    public ChatClient(ServerInfo serverInfo) throws Exception {
        this.serverInfo = serverInfo;

        kryoClient = new Client(65536,     65536 );

        if (debug) {
            System.out.println("[DEBUG] Server PublicKey (Base64): " + Base64.getEncoder().encodeToString(serverInfo.getPublicKey().getEncoded()));
        }

        var kryo = kryoClient.getKryo();
        for (var messageType : MessageTypeIndex.getAllMessageTypes()) {
            kryo.register(messageType);
        }

        kryoClient.addListener(new Listener() {

            //perform handshake right after connection
            @Override public void connected(Connection c) {
                connection = c;

                try {
                    //generate aes key for session
                    sessionSecret = CryptoUtil.generateAESKey();
                    byte[] aesBytes = sessionSecret.getEncoded();

                    if (debug) {
                        System.out.println("[DEBUG][Handshake] AES key (Base64): " + Base64.getEncoder().encodeToString(aesBytes));
                    }

                    //encrypt aes key with rsa (public server key)
                    byte[] encKey = CryptoUtil.encryptRSA(aesBytes, serverInfo.getPublicKey());
                    KeyExchangeMessage kx = new KeyExchangeMessage();
                    kx.sessionId = 0;
                    kx.encryptedKey = encKey;

                    //send to server our encrypted aes key
                    connection.sendTCP(kx);

                    if (debug) {
                        System.out.println("[DEBUG][Handshake] Encrypted AES key (RSA): " + Base64.getEncoder().encodeToString(encKey));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override public void received(Connection _c, Object obj) {
                if (!(obj instanceof EncryptedMessage em)) return;

                try {
                    byte[] pt = CryptoUtil.decrypt(em.ciphertext, sessionSecret, em.iv);
                    if (debug) {
                        System.out.println("[DEBUG][recvObject] iv  " + Base64.getEncoder().encodeToString(em.iv));
                        System.out.println("[DEBUG][recvObject] ct  " + Base64.getEncoder().encodeToString(em.ciphertext));
                        System.out.println("[DEBUG][recvObject] pt  " + Base64.getEncoder().encodeToString(pt));
                    }

                    // deserialize
                    Input input = new Input(pt);
                    Object message = kryo.readClassAndObject(input);
                    input.close();

                    if (!(message instanceof NetworkedMessage nwm)) {
                        System.out.println("[ERROR] Received unknown message type: " + message.getClass().getSimpleName());
                        return;
                    }

                    // check sequence number
                    if (nwm.seqNumber <= serverSeqNumber) {
                        // discard the message
                        System.out.println("[DEBUG][recvObject] Discarding message with wrong seqNumber: " + nwm.getClass().getSimpleName());
                        return;
                    }

                    dispatchResponse(nwm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connect() throws IOException {
        //start client and connect to server
        kryoClient.start();
        kryoClient.connect(5000, serverInfo.getAddress(), serverInfo.getTcpPort());
    }

    // helper ----------------------------------------------------
    private byte[] deriveMasterKey(String pwd, String handleLower) {
        return CryptoUtil.kdf(pwd.getBytes(StandardCharsets.UTF_8),
                handleLower.getBytes(StandardCharsets.UTF_8));
    }
    private byte[] deriveAuthKey(byte[] masterKey, String pwd) {
        return CryptoUtil.kdf(masterKey, pwd.getBytes(StandardCharsets.UTF_8));
    }


    // --------------------- send messages to server methods --------------------- //

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public List<UserInfo> getFriendList() {
        return friendList;
    }

    // public API ------------------------------------------------
    public void register(String handle, String masterPwd, String nickname)
    {
        try {
            // 1) KDFs
            String handleLower  = handle.toLowerCase();
            byte[] masterKey    = deriveMasterKey(masterPwd, handleLower);
            byte[] authKey      = deriveAuthKey(masterKey, masterPwd);

            // 2) key-pair
            KeyPair kp          = CryptoUtil.generateRSA4096KeyPair(); // helper added in CryptoUtil
            byte[] pubDer       = kp.getPublic().getEncoded();
            byte[] privDer      = kp.getPrivate().getEncoded();

            // 3) AES-GCM
            byte[] aesKeyBytes  = Arrays.copyOf(masterKey, 32);          // 32-byte AES-256 key
            SecretKey aesKey    = new SecretKeySpec(aesKeyBytes, "AES");

            byte[] iv        = CryptoUtil.generateIV();
            byte[] cipherTag = CryptoUtil.encrypt(privDer, aesKey, iv);
            byte[] encPrivKey = ByteBuffer.allocate(iv.length + cipherTag.length)
                    .put(iv).put(cipherTag).array();

            // 4) build & send
            RegisterRequest rr  = new RegisterRequest();
            rr.handle           = handle;
            rr.nickname         = nickname;
            rr.authKey          = authKey;
            rr.clientPublicKey  = pubDer;
            rr.encClientPrivKey = encPrivKey;
            sendEncryptedObject(rr);
        } catch(Exception ex){ex.printStackTrace();}
    }

    public void login(String handle, String masterPwd)
    {
        try {
            String handleLower  = handle.toLowerCase();
            byte[] masterKey    = deriveMasterKey(masterPwd, handleLower);
            byte[] authKey      = deriveAuthKey(masterKey, masterPwd);

            LoginRequest lr     = new LoginRequest();
            lr.handle           = handle;
            lr.authKey          = authKey;
            sendEncryptedObject(lr);

            // keep masterKey in memory for challenge phase
            this.pendingLoginMasterKey = masterKey;
            this.pendingHandleLower    = handleLower;
        } catch(Exception ex){ex.printStackTrace();}
    }

    public void createRoom(String roomName, String... handles){
        RequestCreateRoom r = new RequestCreateRoom();
        r.roomName      = roomName;
        r.memberHandles = handles;
        sendEncryptedObject(r);
    }

    public void sendMessage(long roomId, String plainText) {
       // get the room
        RoomInfo info = rooms.keySet()                    // rooms = HashMap<RoomInfo, List<UserInfo>>
                .stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElse(null);

        if (info == null) {
            System.err.println("[sendMessage] Unknown room id " + roomId);
            return;
        }

        try {
            // generate random iv
            byte[] iv = CryptoUtil.generateIV();          // 12-B

            // get the room AES key
            SecretKey roomKey = new SecretKeySpec(info.getEncryptionKey(), "AES");

            // cipher
            byte[] ct = CryptoUtil.encrypt(
                    plainText.getBytes(StandardCharsets.UTF_8),
                    roomKey,
                    iv
            );

            // iv and ciphertext
            byte[] encPayload = ByteBuffer.allocate(iv.length + ct.length)
                    .put(iv).put(ct)
                    .array();

            SendMessage msg = new SendMessage();
            msg.roomId = info.getId();
            msg.encryptedContent = encPayload;
            sendEncryptedObject(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //request all users to server
    public void requestAllUsers() {
        if (debug) {
            System.out.println("[DEBUG] Requesting all users from server");
        }

        AllUserInfoRequest request = new AllUserInfoRequest();
        sendEncryptedObject(request);
    }

    // --------------------- handle methods --------------------- //

    private HashMap<Class<?>, Consumer<?>> responseOneshotCallbacks = new HashMap<>();

    public <T> void setOneshotCallback(Class<T> type, Consumer<T> callback) {
        responseOneshotCallbacks.put(type, callback);
    }

    private <T> void handleResponse(T response) {
        Class<?> type = response.getClass();
        Consumer<?> callback = responseOneshotCallbacks.get(type);

        if (debug) {
            System.out.println("[DEBUG][handleResponse] " + type.getSimpleName() + " : " + response);
        }

        if (callback != null) {
            responseOneshotCallbacks.remove(type);
            ((Consumer<T>) callback).accept(response);
        }
    }

    private void handleAllUserInfoResponse(AllUserInfoResponse response) {
        if (debug) {
            System.out.println("[DEBUG][handleAllUserInfoResponse] Received " + response.getUserInfos().length + " users");
        }

        friendList.clear();
        for (UserInfo userInfo : response.getUserInfos()) {
            if (userInfo.getHandle().equals(currentUser.getHandle())) continue; // skip self
            friendList.add(userInfo);
        }

        WindowManager.showLobby();
        //friendList.addAll(response.userInfos);
    }


    private void dispatchResponse(NetworkedMessage message) {
        System.out.println("[DEBUG][dispatchResponse] " + message.getClass().getSimpleName());

        serverSeqNumber = message.seqNumber;

        switch (message) {
            case SessionHelloMessage hello -> {
                sessionId = hello.sessionId;
                if (debug) {
                    System.out.println("[DEBUG][dispatchResponse] SessionHelloMessage: " + hello.sessionId);
                }
            }
            case RegisterResponse resp -> handleResponse(resp);
            case LoginChallenge ch -> {
                try {
                    byte[] iv   = Arrays.copyOfRange(ch.encClientPrivKey, 0, 12);
                    byte[] ct   = Arrays.copyOfRange(ch.encClientPrivKey, 12, ch.encClientPrivKey.length);

                    byte[] aesKeyBytes = Arrays.copyOf(pendingLoginMasterKey, 32);
                    SecretKey aesKey   = new SecretKeySpec(aesKeyBytes, "AES");

                    byte[] priv = CryptoUtil.decrypt(ct, aesKey, iv);

                    PrivateKey privKey = KeyFactory.getInstance("RSA")
                            .generatePrivate(new PKCS8EncodedKeySpec(priv));

                    myPrivateKey = privKey;

                    byte[] sig  = CryptoUtil.signWithRsa4096(privKey, ch.challenge);

                    LoginChallengeResponse resp = new LoginChallengeResponse();
                    resp.challengeSignature = sig;
                    sendEncryptedObject(resp);
                } catch (Exception e) { e.printStackTrace(); }
            }
            case LoginResponse resp -> {
                if (resp.success)
                {
                    currentUser = new UserInfo(resp.userInfo.getId(),resp.userInfo.getHandle(), resp.userInfo.getUsername(),
                            resp.userInfo.getBio(), resp.userInfo.getAvatarPath(), resp.userInfo.getPublicKey());

                    //send request for all users
                    requestAllUsers();
                    sendEncryptedObject(new RequestRoomList());

                    if (debug) {
                        System.out.println("[DEBUG] Login sucessful, user info: " + currentUser.getHandle());
                    }
                }

                handleResponse(resp);

                //System.out.println("[DEBUG][dispatchResponse] LoginResponse: " + resp.success + ", " + resp.message);
            }
            case AllUserInfoResponse resp -> {
                handleAllUserInfoResponse(resp);
            }
            case CreateRoom cr -> handleCreateRoom(cr);
            case RoomList rl  -> handleLoadRooms(rl);
            case ReceivedMessage rm -> handleShowIncomingMessage(rm);
            case NewUserCreated nu -> handleNewUserCreated(nu);


            default -> System.out.println("[WARN] Unhandled NetworkedMessage from server: " + message.getClass());
        }
    }

    private void handleNewUserCreated(NewUserCreated nu) {
        if (debug) {
            System.out.println("[DEBUG][handleNewUserCreated] New user created: " + nu.getUserInfo().getHandle());
        }

        // add the new user to the friend list
        UserInfo newUser = nu.getUserInfo();
        if (!friendList.contains(newUser)) {
            friendList.add(newUser);
            if (WindowManager.state == CurrentUIState.LOBBY) {
                WindowManager.showLobby(); // refresh the lobby UI
            }
        } else {
            System.out.println("[WARN] User already exists in friend list: " + newUser.getHandle());
        }
    }

    private void handleShowIncomingMessage(ReceivedMessage rm) {
        //get the room
        RoomInfo info = rooms.keySet()
                .stream()
                .filter(r -> r.getId() == rm.roomId)
                .findFirst()
                .orElse(null);

        if (info == null) {
            System.err.println("Room " + rm.roomId + " unknown.");
            return;
        }

        // split iv and ciphertext
        byte[] payload = rm.encryptedContent;
        if (payload.length < 12) return;            // corrupción
        byte[] iv = Arrays.copyOfRange(payload, 0, 12);
        byte[] ct = Arrays.copyOfRange(payload, 12, payload.length);

        // get aes key of the room
        SecretKey roomKey = new SecretKeySpec(info.getEncryptionKey(), "AES");

        try {
            // decrypt the message
            byte[] plain = CryptoUtil.decrypt(ct, roomKey, iv);
            String text  = new String(plain, StandardCharsets.UTF_8);
            RoomInfo.Message message = new RoomInfo.Message(
                    rm.roomId,
                    rm.authorHandle,
                    rm.createdAt,
                    text
            );

            // show ui
            System.out.println("[" + rm.roomId + "] <" + rm.authorHandle + ">: " + text);
            info.addMessage(message);
            WindowManager.showIncomingMessage(message);

            if (WindowManager.state == CurrentUIState.RECENT) {
                WindowManager.showChat();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void handleLoadRooms(RoomList rl) {

        if (myPrivateKey == null) {
            System.err.println("[RoomList] privateKey not available yet");
            return;
        }

        /* 0) índice rápido de usuarios para mapear authorId → handle */
        Map<Long, String> idToHandle = new HashMap<>();
        idToHandle.put(currentUser.getId(), currentUser.getHandle());
        for (UserInfo f : friendList) idToHandle.put(f.getId(), f.getHandle());

        rooms.clear();

        for (RoomDTO dto : rl.rooms) {
            try {

                byte[] roomKeyBytes = CryptoUtil.decryptRSA(dto.encKey, myPrivateKey);
                SecretKey roomKey = new SecretKeySpec(roomKeyBytes, "AES");

                RoomInfo info = new RoomInfo(
                        dto.roomId,
                        dto.roomName,
                        roomKeyBytes,
                        dto.memberHandles
                );

                // load room messages history
                if (dto.messages != null) {
                    for (MsgDTO m : dto.messages) {

                        if (m.encryptedContent == null || m.encryptedContent.length < 12) continue;

                        byte[] iv = Arrays.copyOfRange(m.encryptedContent, 0, 12);
                        byte[] ct = Arrays.copyOfRange(m.encryptedContent, 12, m.encryptedContent.length);

                        byte[] plain = CryptoUtil.decrypt(ct, roomKey, iv);
                        String text  = new String(plain, StandardCharsets.UTF_8);

                        String authorHandle = idToHandle.getOrDefault(m.authorId, "user#" + m.authorId);

                        info.addMessage(
                                new RoomInfo.Message(
                                        m.id,
                                        authorHandle,
                                        m.createdAt,
                                        text
                                )
                        );
                    }
                }

                //save room participants
                List<UserInfo> participants = new ArrayList<>();
                for (String h : dto.memberHandles) participants.add(
                        h.equals(currentUser.getHandle())
                                ? currentUser
                                : friendList.stream()
                                .filter(u -> u.getHandle().equals(h))
                                .findFirst()
                                .orElse(new UserInfo(-1,h, h,"","",null))
                );
                rooms.put(info, participants);

                System.out.printf("[RoomList] loaded room #%d with %d messages%n", dto.roomId, info.getMessages().size());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

       //ui update
    }


    private void handleCreateRoom(CreateRoom cr) {

        if (myPrivateKey == null) {
            System.err.println("[CreateRoom] privateKey not available.");
            return;
        }

        // decrypt the room key using our private key
        byte[] roomKeyBytes;
        try {
            roomKeyBytes = CryptoUtil.decryptRSA(cr.encKeyForMe, myPrivateKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        SecretKey roomKey = new SecretKeySpec(roomKeyBytes, "AES");

        // create RoomInfo object
        RoomInfo info = new RoomInfo(
                cr.roomId,
                cr.roomName == null ? "" : cr.roomName,
                roomKeyBytes,
                cr.memberHandles
        );

        // get participants
        List<UserInfo> participants = new ArrayList<>();
        for (String h : cr.memberHandles) {
            UserInfo ui =
                    h.equals(currentUser.getHandle())
                            ? currentUser
                            : friendList.stream()
                            .filter(f -> f.getHandle().equals(h))
                            .findFirst()
                            .orElse(new UserInfo(-1, h, h, "", "", null));
            participants.add(ui);
        }

        // register room in the client
        rooms.putIfAbsent(info, participants);

        System.out.printf("[CreateRoom] room #%d created → %s%n", cr.roomId, Arrays.toString(cr.memberHandles));

        //update ui
        if (WindowManager.state == CurrentUIState.RECENT)
        {
            WindowManager.showChat();
        }
    }

    // helper to send any encrypted object
    private void sendEncryptedObject(NetworkedMessage obj) {
        clientSeqNumber++;
        obj.seqNumber = clientSeqNumber;

        try {
            // serialize with kryo
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Output output = new Output(baos);
            kryoClient.getKryo().writeClassAndObject(output, obj);
            output.close();

            byte[] plaintext = baos.toByteArray();

            // encrypt
            byte[] iv = CryptoUtil.generateIV();
            byte[] ct = CryptoUtil.encrypt(plaintext, sessionSecret, iv);

            EncryptedMessage em = new EncryptedMessage();
            em.sessionId = sessionId;
            em.iv = iv;
            em.ciphertext = ct;

            connection.sendTCP(em);

            if (debug) {
                System.out.println("[DEBUG][sendObject] iv  " + Base64.getEncoder().encodeToString(iv));
                System.out.println("[DEBUG][sendObject] ct  " + Base64.getEncoder().encodeToString(ct));
                System.out.println("[DEBUG][sendObject] obj " + obj.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // --------------------- setter, getter methods --------------------- //
    public UserInfo getUserInfo() {
        return currentUser;
    }

    public void setUsername(String username) {
        if (username != null) {
            currentUser.setUsername(username);
        }
    }

    public void setBio(String bio) {
        currentUser.setBio(bio);
    }

    public RoomInfo getPrivateRoomInfo(long userId) {


        String myHandle = currentUser.getHandle();

        String targetHandle;
        if (userId == currentUser.getId()) {
            targetHandle = myHandle;
        } else {
            targetHandle = friendList.stream()
                    .filter(u -> u.getId() == userId)
                    .map(UserInfo::getHandle)
                    .findFirst()
                    .orElse(null);
            if (targetHandle == null) return null;
        }

        if (myHandle.equals(targetHandle)) { //personal
            return rooms.keySet()
                    .stream()
                    .filter(r -> r.getMemberHandles() != null && r.getMemberHandles().length == 1
                            && r.getMemberHandles()[0].equals(myHandle))
                    .findFirst()
                    .orElse(null);
        }

        for (RoomInfo r : rooms.keySet()) {
            String[] members = r.getMemberHandles();
            if (members == null) continue;

            //private
            if (members.length == 2) {
                boolean hasMe = members[0].equals(myHandle) || members[1].equals(myHandle);
                boolean hasTarget = members[0].equals(targetHandle) || members[1].equals(targetHandle);
                if (hasMe && hasTarget) return r;
            }
        }
        return null;
    }
}
