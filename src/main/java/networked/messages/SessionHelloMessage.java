package networked.messages;

/**
 * @brief 세션 핸드쉐이크를 위한 메시지. 클라이언트로부터 AES 키를 전송받은 뒤, 서버가 세션 ID를 전송하기 위해 사용한다.
 */
public class SessionHelloMessage extends NetworkedMessage {
    /**
     * @brief 서버가 생성해서 클라이언트에게 전송하는 세션 ID.
     */
    public long sessionId;
}
