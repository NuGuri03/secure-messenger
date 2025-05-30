package networked.messages;

public class EncryptedMessage extends PreSessionMessage {
    public byte[] iv;
    public byte[] ciphertext;
}
