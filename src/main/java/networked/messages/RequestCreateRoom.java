package networked.messages;

public class RequestCreateRoom extends NetworkedMessage {
    public String roomName;
    public String[] memberHandles;
    public byte[][] encryptedKeys;
}
