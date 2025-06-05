package networked.messages;

public class SendMessage extends NetworkedMessage {
    public long roomId;
    public byte[] encryptedContent;
}
