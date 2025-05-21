package client;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.*;
import common.*;
import crypto.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

public class ChatClient {
    public static final String HOST = "localhost";
    public static final int TCP_PORT = 54555, UDP_PORT = 54777;

    private final Client kryoClient;
    private final PublicKey serverPublicKey;
    private SecretKey aesKey;
    private final boolean debug = true;

    public ChatClient() throws Exception {
        kryoClient = new Client();

        //load server public key from pem
        String baseDir = System.getProperty("user.dir");
        String pubPath = baseDir + File.separator + "server_public.pem";
        serverPublicKey = CryptoUtil.loadRSAPublicKey(pubPath);
        if (debug) {
            System.out.println("[DEBUG] Server PublicKey (Base64): " + Base64.getEncoder().encodeToString(serverPublicKey.getEncoded()));}

        var kryo = kryoClient.getKryo();
        kryo.register(byte[].class);
        kryo.register(KeyExchange.class);
        kryo.register(EncryptedMessage.class);
        kryo.register(RegisterRequest.class);
        kryo.register(RegisterResponse.class);
        kryo.register(LoginRequest.class);
        kryo.register(LoginResponse.class);

        kryoClient.addListener(new Listener() {

            //perform handshake right after connection
            @Override public void connected(Connection c) {
                try {
                    //generate aes key for session
                    aesKey = CryptoUtil.generateAESKey();
                    byte[] aesBytes = aesKey.getEncoded();
                    if (debug) {
                        System.out.println("[DEBUG][Handshake] AES key (Base64): " + Base64.getEncoder().encodeToString(aesBytes));}

                    //encrypt aes key with rsa (public server key)
                    byte[] encKey = CryptoUtil.encryptRSA(aesBytes, serverPublicKey);
                    KeyExchange kx = new KeyExchange();
                    kx.encryptedKey = encKey;

                    //send to server our encrypted aes key
                    c.sendTCP(kx);

                    if (debug) {
                        System.out.println("[DEBUG][Handshake] Encrypted AES key (RSA): " + Base64.getEncoder().encodeToString(encKey));}

                    //(test) send first encrypted message after handshake
                    login("noru", "noru", c);
                    register("noru", "noru", c);
                    login("noru", "noru", c);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override public void received(Connection c, Object obj) {
                if (!(obj instanceof EncryptedMessage em)) return;
                try {
                    byte[] pt = CryptoUtil.decrypt(em.ciphertext, aesKey, em.iv);
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
        kryoClient.connect(5000, HOST, TCP_PORT, UDP_PORT);
    }

    //helper to send any object encrypted
    private void sendEncryptedObject(Object obj, Connection c) {
        try {
            // serialize with kryo
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            kryoClient.getKryo().writeClassAndObject(output, obj);
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

    // --------------------- send messages to server methods --------------------- //

    public void register(String user, String pass, Connection c) {
        RegisterRequest rr = new RegisterRequest();
        rr.username = user;
        rr.password = pass;
        sendEncryptedObject(rr, c);
    }

    public void login(String user, String pass, Connection c) {
        LoginRequest lr = new LoginRequest();
        lr.username = user;
        lr.password = pass;
        sendEncryptedObject(lr, c);
    }

    // --------------------- handle methods --------------------- //

    private void handleRegisterResponse(RegisterResponse response)
    {
        System.out.println("Register response: " + response.success + " : " + response.message);
    }

    private void handleLoginResponse(LoginResponse response)
    {
        System.out.println("Login response: " + response.success + " : " + response.message);
    }

}
