package server;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;
import common.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import crypto.CryptoUtil;

public class ChatServer {
    public static final int TCP_PORT = 54555, UDP_PORT = 54777;
    private final Server kryoServer;
    private final PrivateKey serverPrivateKey;
    private final Map<Connection, SecretKey> aesKeys = new ConcurrentHashMap<>();
    private final boolean debug = true;

    //temporal test db, change later to real db
    private final Map<String,String> usersDb = new ConcurrentHashMap<>();

    public ChatServer() throws Exception {

        //load or generate rsa keypair
        String baseDir = System.getProperty("user.dir");
        String privPath = baseDir + File.separator + "server_private.pem";
        String pubPath  = baseDir + File.separator + "server_public.pem";
        KeyPair kp = CryptoUtil.loadOrGenerateRSAKeyPair(privPath, pubPath);
        serverPrivateKey = kp.getPrivate();
        if (debug) {
            System.out.println("[DEBUG] Server PrivateKey (Base64): " + Base64.getEncoder().encodeToString(serverPrivateKey.getEncoded()));
            System.out.println("[DEBUG] Server PublicKey  (Base64): " + Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        }

        kryoServer = new Server();
        var kryo = kryoServer.getKryo();
        kryo.register(byte[].class);
        kryo.register(KeyExchange.class);
        kryo.register(EncryptedMessage.class);
        kryo.register(RegisterRequest.class);
        kryo.register(RegisterResponse.class);
        kryo.register(LoginRequest.class);
        kryo.register(LoginResponse.class);

        kryoServer.addListener(new Listener() {
            @Override public void connected(Connection c) {
                //log client connection
                System.out.println("Client connected: " + c.getRemoteAddressTCP());
            }
            @Override public void disconnected(Connection c) {
                //log client disconnection
                System.out.println("Client disconnected: " + c.getRemoteAddressTCP());
            }
            @Override public void received(Connection c, Object obj) {
                try {
                    // 1) handshake, receive client AES key (comes encrypted using server public RSA key)
                    if (obj instanceof KeyExchange kx) {
                        //decrypt handshake using server rsa privatekey
                        byte[] aesBytes = CryptoUtil.decryptRSA(kx.encryptedKey, serverPrivateKey);
                        SecretKey aes = new SecretKeySpec(aesBytes, "AES");
                        aesKeys.put(c, aes);

                        if (debug) {
                            System.out.println("[DEBUG][Handshake] Encrypted AES key (RSA): " + Base64.getEncoder().encodeToString(kx.encryptedKey));
                            System.out.println("[DEBUG][Handshake] Decrypted AES key (Base64): " + Base64.getEncoder().encodeToString(aesBytes));
                        }
                        return;
                    }

                    // 2) encrypted payload
                    if (obj instanceof EncryptedMessage em) {
                        SecretKey aes = aesKeys.get(c);
                        byte[] plaintext = CryptoUtil.decrypt(em.ciphertext, aes, em.iv);
                        if (debug) {
                            System.out.println("[DEBUG][Recv] IV:  " + Base64.getEncoder().encodeToString(em.iv));
                            System.out.println("[DEBUG][Recv] CT:  " + Base64.getEncoder().encodeToString(em.ciphertext));
                        }

                        // 3) deserialize inner object
                        Input input = new Input(plaintext);
                        Object message = kryoServer.getKryo().readClassAndObject(input);
                        input.close();

                        // 4) dispatch by type
                        if (message instanceof RegisterRequest rr) {
                            handleRegister(rr, c);
                        }
                        else if (message instanceof LoginRequest lr) {
                            handleLogin(lr, c);
                        }
                        //add more types here

                        else {
                            // echo fallback
                            String text = new String(plaintext, StandardCharsets.UTF_8);
                            if (debug) System.out.println("[DEBUG][Recv] raw text: " + text);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //helper to send any object encrypted
    private void sendEncryptedObject(Object obj, Connection c) {
        SecretKey aesKey = aesKeys.get(c);
        if (aesKey == null) throw new IllegalStateException("AES key not found for connection: " + c);

        try {
            // serialize with kryo
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            kryoServer.getKryo().writeClassAndObject(output, obj);
            output.close();
            byte[] plaintext = baos.toByteArray();

            // encrypt
            byte[] iv = CryptoUtil.generateIV();
            byte[] ct = CryptoUtil.encrypt(plaintext, aesKey, iv);
            EncryptedMessage em = new EncryptedMessage();
            em.iv = iv;
            em.ciphertext = ct;
            c.sendTCP(em);

            if (debug) {
                System.out.println("[DEBUG][sendObject] iv  " + Base64.getEncoder().encodeToString(iv));
                System.out.println("[DEBUG][sendObject] ct  " + Base64.getEncoder().encodeToString(ct));
                System.out.println("[DEBUG][sendObject] obj " + obj.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() throws IOException {
        //start server and bind ports
        kryoServer.start();
        kryoServer.bind(TCP_PORT, UDP_PORT);
        System.out.printf("Server started on TCP %d / UDP %d%n", TCP_PORT, UDP_PORT);
    }

    // --------------------- handle methods --------------------- //

    //temporal method, check with DB later
    private void handleRegister(RegisterRequest req, Connection c) {
        RegisterResponse resp = new RegisterResponse();
        if (usersDb.containsKey(req.username)) {
            resp.success = false;
            resp.message = "username already exists";
        } else {
            usersDb.put(req.username, req.password);
            resp.success = true;
            resp.message = "registration successful";
        }
        sendEncryptedObject(resp, c);
    }

    //temporal method, check with DB later
    private void handleLogin(LoginRequest req, Connection c) {
        LoginResponse resp = new LoginResponse();
        String stored = usersDb.get(req.username);
        if (stored != null && stored.equals(req.password)) {
            resp.success = true;
            resp.message = "login successful";
        } else {
            resp.success = false;
            resp.message = "invalid credentials";
        }
        sendEncryptedObject(resp, c);
    }
}
