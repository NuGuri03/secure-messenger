package server;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import networked.*;
import networked.messages.EncryptedMessage;
import networked.messages.KeyExchangeMessage;
import networked.messages.LoginRequest;
import networked.messages.LoginResponse;
import networked.messages.NetworkedMessage;
import networked.messages.PreSessionMessage;
import networked.messages.RegisterRequest;
import networked.messages.RegisterResponse;
import networked.messages.SessionHelloMessage;
import server.Database.UserBuilder;
import server.models.User;

public class ChatServer {
    public static final int TCP_PORT = 23456;

    private final Server kryoServer;
    private final PrivateKey serverPrivateKey;

    private final Map<Long, SessionInfo> sessionTable = new ConcurrentHashMap<>();
    private final Map<Connection, Long> connectionTable = new ConcurrentHashMap<>();


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
        var kryoServer = new Server();

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

            default -> System.out.println("[ChatServer] [WARN] Unhandled NetworkedMessage: " + msg.getClass());
        }
    }

    // --------------------- handle methods --------------------- //

    //temporal method, check with DB later
    private void handleRegister(RegisterRequest req, Connection c) throws Exception {
        RegisterResponse resp = new RegisterResponse();

        if (User.isHandleExists(req.username)) {
            resp.success = false;
            resp.message = "handle already exists";
            sendMessage(resp, c);
            return;
        }

        if (!User.isHandleValid(req.username)) {
            resp.success = false;
            resp.message = "invalid handle";
            sendMessage(resp, c);
            return;
        }

        // TODO: RegisterRequest should contain more fields like nickname, public key, etc.
        var handle = req.username; // placeholder, should be req.handle
        var nickname = req.username; // placeholder, should be req.nickname
        var authenticationKey = req.password.getBytes(Charset.forName("UTF-8")); // placeholder
        var publicKey = new byte[] {0x1, 0x2, 0x3, 0x4, 0x5}; // placeholder
        var encryptedPrivateKey = new byte[] {0x11, 0x22, 0x33, 0x44, 0x55}; // placeholder
        var encryptedPrivateKeyIv = new byte[] {0xA, 0xB, 0xC, 0xD, 0xE}; // placeholder

        new UserBuilder()
            .setHandle(handle)
            .setNickname(nickname)
            .setAuthenticationKey(authenticationKey)
            .setPublicKey(publicKey)
            .setEncryptedPrivateKey(encryptedPrivateKey)
            .setEncryptedPrivateKeyIv(encryptedPrivateKeyIv)
            .createUser();

        resp.success = true;
        resp.message = "registration successful";
        sendMessage(resp, c);
    }

    //temporal method, check with DB later
    private void handleLogin(LoginRequest req, Connection c) {
        LoginResponse resp = new LoginResponse();

        User user = User.queryByHandle(req.username);

        // TODO: placeholder; use authenticationKey, not password
        var authenticationKey = req.password.getBytes(Charset.forName("UTF-8"));
        if (user != null && user.verifyAuthenticationKey(authenticationKey)) {
            resp.success = true;
            resp.message = "login successful";
        } else {
            resp.success = false;
            resp.message = "invalid credentials";
        }

        sendMessage(resp, c);
    }
}
