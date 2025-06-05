package networked.messages;

public class LoginChallenge extends NetworkedMessage {
    public byte[] clientPublicKey;     // X.509 DER
    public byte[] encClientPrivKey;    // IV‖ciphertext‖tag
    public byte[] challenge;           // 16 random bytes
}
