package networked.messages;

public class CreateRoom extends NetworkedMessage {
    public long roomId;
    public String roomName;
    public String[] memberHandles;
    public byte[] encKeyForMe; // encrypted key for this user
}
