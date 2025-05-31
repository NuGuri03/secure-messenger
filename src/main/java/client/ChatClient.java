package client;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import networked.*;
import networked.messages.EncryptedMessage;
import networked.messages.KeyExchangeMessage;
import networked.messages.LoginRequest;
import networked.messages.LoginResponse;
import networked.messages.NetworkedMessage;
import networked.messages.RegisterRequest;
import networked.messages.RegisterResponse;
import networked.messages.SessionHelloMessage;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
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


    public ChatClient(ServerInfo serverInfo) throws Exception {
        this.serverInfo = serverInfo;

        kryoClient = new Client();

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

    // --------------------- send messages to server methods --------------------- //

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void register(String user, String pass) {
        RegisterRequest rr = new RegisterRequest();
        rr.username = user;
        rr.password = pass;
        sendEncryptedObject(rr);
    }

    public void login(String user, String pass) {
        LoginRequest lr = new LoginRequest();
        lr.username = user;
        lr.password = pass;
        sendEncryptedObject(lr);
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
            case LoginResponse resp -> {
                // TODO: 예시로 사용자 설정
                currentUser = new UserInfo("@testUser", "Test User", "Hello world", null, null);
                handleResponse(resp);
            }
            default -> System.out.println("[WARN] Unhandled NetworkedMessage from server: " + message.getClass());
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
}
