package networked.messages;

public class LoginChallengeResponse extends NetworkedMessage {
    public byte[] challengeSignature;  // ~512 bytes
}
