package networked.messages;

public class LoginRequest extends NetworkedMessage {
    public String handle;
    public byte[] authKey; // 64 bytes
}
