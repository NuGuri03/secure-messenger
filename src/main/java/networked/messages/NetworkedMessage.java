package networked.messages;

/**
 * @brief 네트워크로 오가는 모든 메시지의 부모 클래스.
 */
public abstract class NetworkedMessage {
    /**
     * @brief 메시지의 Sequence Number. 보낼 때마다 1씩 증가한다.
     * @implNote 마지막으로 받은 Sequence Number보다 커야 한다. 그렇지 않으면 이 메시지는 폐기된다.
     */
    public int seqNumber;
}
