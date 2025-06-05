package networked.messages;

public class RegisterRequest extends NetworkedMessage {
    public String  handle;
    public String  nickname;
    public byte[]  authKey;          // 64 bytes
    public byte[]  clientPublicKey;  // X.509 DER
    public byte[]  encClientPrivKey; // IV‖ciphertext‖tag
}
