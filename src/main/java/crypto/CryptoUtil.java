package crypto;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class CryptoUtil {
    private static final String AES           = "AES";
    private static final String AES_GCM       = "AES/GCM/NoPadding";
    private static final int    GCM_TAG_BITS  = 128;
    private static final int    GCM_IV_BYTES  = 12;

    //generate aes-256 key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(AES);
        kg.init(256);
        return kg.generateKey();
    }

    //generate random iv of 12 bytes
    public static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_BYTES];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    //encrypt plaintext with aes-gcm
    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] iv) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, iv);
        c.init(Cipher.ENCRYPT_MODE, key, spec);
        return c.doFinal(plaintext);
    }

    //decrypt ciphertext with aes-gcm
    public static byte[] decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, iv);
        c.init(Cipher.DECRYPT_MODE, key, spec);
        return c.doFinal(ciphertext);
    }

    //encrypt data with rsa oaep-sha256
    public static byte[] encryptRSA(byte[] data, PublicKey pub) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, pub);
        return c.doFinal(data);
    }

    //decrypt data with rsa oaep-sha256
    public static byte[] decryptRSA(byte[] data, PrivateKey priv) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, priv);
        return c.doFinal(data);
    }

    //load rsa public key from pem x509
    public static PublicKey loadRSAPublicKey(String pemPath) throws IOException, GeneralSecurityException {
        try (PemReader reader = new PemReader(new FileReader(pemPath))) {
            PemObject pem = reader.readPemObject();
            byte[] content = pem.getContent();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }

    //load rsa private key from pem pkcs#8
    public static PrivateKey loadRSAPrivateKey(String pemPath) throws IOException, GeneralSecurityException {
        try (PemReader reader = new PemReader(new FileReader(pemPath))) {
            PemObject pem = reader.readPemObject();
            byte[] content = pem.getContent();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    //load or generate rsa keypair and save to pem files
    public static KeyPair loadOrGenerateRSAKeyPair(String privPath, String pubPath) throws Exception {
        if (Files.exists(Paths.get(privPath)) && Files.exists(Paths.get(pubPath))) {
            PrivateKey priv = loadRSAPrivateKey(privPath);
            PublicKey  pub  = loadRSAPublicKey(pubPath);
            return new KeyPair(pub, priv);
        }
        //generate new rsa keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        //save private key to pem
        try (Writer w = new FileWriter(privPath)) {
            String b64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
            w.write("-----BEGIN PRIVATE KEY-----\n" + chunk(b64) + "-----END PRIVATE KEY-----\n");
        }
        //save public key to pem
        try (Writer w = new FileWriter(pubPath)) {
            String b64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
            w.write("-----BEGIN PUBLIC KEY-----\n" + chunk(b64) + "-----END PUBLIC KEY-----\n");
        }
        return kp;
    }

    //helper to chunk base64 into lines of 64 chars
    private static String chunk(String base64) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < base64.length()) {
            int end = Math.min(base64.length(), i + 64);
            sb.append(base64, i, end).append("\n");
            i = end;
        }
        return sb.toString();
    }
}
