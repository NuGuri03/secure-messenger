package networked;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    /**
     * 무작위의 long값을 반환한다.
     * @apiNote long은 64비트의 정수형이므로, 경우의 수는 2^64 = 18,446,744,073,709,551,616이다.
     */
    public static long generateRandomId() {
        return new SecureRandom().nextLong();
    }

    /**
     * 지정된 길이의 무작위 바이트 배열을 생성한다.
     * @param length 생성할 바이트 배열의 길이
     * @return 무작위 바이트 배열
     */
    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
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
    public static PublicKey loadRSAPublicKey(Reader dataReader) throws IOException, GeneralSecurityException {
        try (PemReader reader = new PemReader(dataReader)) {
            PemObject pem = reader.readPemObject();
            byte[] content = pem.getContent();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }

    //load rsa private key from pem pkcs#8
    public static PrivateKey loadRSAPrivateKey(Reader dataReader) throws IOException, GeneralSecurityException {
        try (PemReader reader = new PemReader(dataReader)) {
            PemObject pem = reader.readPemObject();
            byte[] content = pem.getContent();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(content);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    public static KeyPair generateRSA4096KeyPair() throws GeneralSecurityException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(4096);
        return kpg.generateKeyPair();
    }

    public static byte[] signWithRsa4096(PrivateKey privKey, byte[] data) throws GeneralSecurityException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verifyWithRsa4096(PublicKey pubKey, byte[] data, byte[] signature) throws GeneralSecurityException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pubKey);
        sig.update(data);
        return sig.verify(signature);
    }

    //load or generate rsa keypair and save to pem files
    public static KeyPair loadOrGenerateRSAKeyPair(Path privPath, Path pubPath) throws Exception {
        if (Files.exists(privPath) && Files.exists(pubPath)) {
            PrivateKey priv;
            PublicKey pub;

            try (FileReader reader = new FileReader(privPath.toFile())) {
                priv = loadRSAPrivateKey(reader);
            }

            try (FileReader reader = new FileReader(pubPath.toFile())) {
                pub = loadRSAPublicKey(reader);
            }

            return new KeyPair(pub, priv);
        }
    
        // generate new rsa keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(4096);
        KeyPair kp = kpg.generateKeyPair();
    
        // save private key to pem
        try (Writer w = new FileWriter(privPath.toFile())) {
            String b64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
            w.write("-----BEGIN PRIVATE KEY-----\n" + chunk(b64) + "-----END PRIVATE KEY-----\n");
        }

        // save public key to pem
        try (Writer w = new FileWriter(pubPath.toFile())) {
            String b64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
            w.write("-----BEGIN PUBLIC KEY-----\n" + chunk(b64) + "-----END PUBLIC KEY-----\n");
        }

        return kp;
    }

    /**
     * Argon2id 키 파생 함수 (KDF)를 사용하여 입력 데이터를 해시한다.
     * @param input 입력 데이터
     * @param salt 솔트 값
     * @return 64바이트 길이의 해시 값
     * @see https://hd.nulable.kr/s/E286YqsWG#%EC%A0%84%EC%A0%9C%EC%A1%B0%EA%B1%B4
     * @apiNote 문서에 명시된 바와 같이, Argon2id(m=19MiB, t=2, p=1, length=64bytes)의 설정을 사용한다.
     * @apiNote 같은 input과 salt에 대해 항상 동일한 해시 값을 생성한다.
     */
    public static byte[] kdf(byte[] input, byte[] salt) {
        var params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withMemoryAsKB(1024 * 19)  // t=19 MiB
            .withIterations(2)   // i=2
            .withParallelism(1)  // p=1
            .withSalt(salt)
            .build();
        
        var argon2 = new Argon2BytesGenerator();
        argon2.init(params);

        byte[] output = new byte[64];  // outputLength = 64 bytes
        argon2.generateBytes(input, output, 0, output.length);

        return output;
    }

    /**
     * 두 바이트 배열을 안전하게 비교한다.
     * @param a 첫 번째 바이트 배열
     * @param b 두 번째 바이트 배열
     * @return 두 배열이 동일하면 true, 그렇지 않으면 false
     * @apiNote 이 메서드는 Timing attack을 방지하기 위해 상수 시간으로 비교를 수행한다.
     */
    public static boolean secureCompareBytes(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        // Compare byte arrays in constant time
        // to avoid possible timing attack vectors
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
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

    public static PublicKey bytesToPub(byte[] der) throws GeneralSecurityException {
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(der));
    }
}
