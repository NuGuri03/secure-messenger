package networked.messages;

public class ReceivedMessage extends NetworkedMessage {
    public long roomId;
    public long createdAt;
    public String authorHandle;
    public byte[] encryptedContent;
}
