package networked.messages;

/**
 * @brief 암호화되지 않은, 즉 NetworkedMessage가 아닌 모든 메시지의 부모 클래스.
 */
public abstract class PreSessionMessage {
    /**
     * @brief 메시지의 Session ID. 서버가 무작위로 발급하는 8-byte 정수이다.
     * @implNote 이 값은 서버가 클라이언트를 구별하는 데 사용한다.
     *  IP가 바뀌었을 경우에도 session ID가 같으면 서버는 이를 동일한 클라이언트로 인식한다.
     */
    public long sessionId;
}
