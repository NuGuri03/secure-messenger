package client;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;

import networked.*;
import networked.messages.EncryptedMessage;
import networked.messages.KeyExchangeMessage;
import networked.messages.LoginRequest;
import networked.messages.LoginResponse;
import networked.messages.RegisterRequest;
import networked.messages.RegisterResponse;

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

    private UserInfo currentUser;


    public ChatClient(ServerInfo serverInfo) throws Exception {
        this.serverInfo = serverInfo;

        kryoClient = new Client();

        if (debug) {
            System.out.println("[DEBUG] Server PublicKey (Base64): " + Base64.getEncoder().encodeToString(serverInfo.getPublicKey().getEncoded()));
        }

        var kryo = kryoClient.getKryo();
        kryo.register(byte[].class);
        kryo.register(KeyExchangeMessage.class);
        kryo.register(EncryptedMessage.class);
        kryo.register(RegisterRequest.class);
        kryo.register(RegisterResponse.class);
        kryo.register(LoginRequest.class);
        kryo.register(LoginResponse.class);

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
                    kx.encryptedKey = encKey;

                    //send to server our encrypted aes key
                    connection.sendTCP(kx);

                    if (debug) {
                        System.out.println("[DEBUG][Handshake] Encrypted AES key (RSA): " + Base64.getEncoder().encodeToString(encKey));
                    }

                    //(test) send first encrypted message after handshake
                    login("noru", "noru");
                    register("noru", "noru");
                    login("noru", "noru");

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
                    }
                    // deserialize
                    Input input = new Input(pt);
                    Object message = kryo.readClassAndObject(input);
                    input.close();

                    if (message instanceof RegisterResponse resp) {
                        // handle server response to register
                        handleRegisterResponse(resp);
                    }
                    else if (message instanceof LoginResponse resp) {
                        handleLoginResponse(resp);
                    }
                    //add more types here


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

    private void handleRegisterResponse(RegisterResponse response)
    {
        System.out.println("Register response: " + response.success + " : " + response.message);
        handleResponse(response);
    }

    private void handleLoginResponse(LoginResponse response)
    {
        System.out.println("Login response: " + response.success + " : " + response.message);
        handleResponse(response);
    }

    // helper to send any encrypted object
    private void sendEncryptedObject(Object obj) {
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
}
