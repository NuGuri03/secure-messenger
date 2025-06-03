package server;

import javax.crypto.SecretKey;

public class SessionInfo {
    private Long sessionId;
    private SecretKey secretKey;
    private String handleLower = null;
    private int recvSeqNumber = -1;
    private int sendSeqNumber = 0;

    public SessionInfo(Long sessionId, SecretKey secretKey) {
        this.sessionId = sessionId;
        this.secretKey = secretKey;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * 받은 메시지의 Sequence Number를 검증하고, 업데이트한다.
     * @param seqNumber 메시지의 seqNumber 값.
     * @return 검증을 통과했다면 true, 그렇지 않으면 false.
     */
    public boolean receiveSeqNumber(Integer seqNumber) {
        if ((seqNumber == null) || (recvSeqNumber >= seqNumber)) {
            return false;
        }

        recvSeqNumber = seqNumber;
        return true;
    }

    public void   setHandleLower(String h){ this.handleLower = h; }
    public String getHandleLower(){ return handleLower; }

    /**
     * 보낼 메시지의 Sequence Number를 받아온다.
     * @return 호출할 때마다 1씩 증가하는 sequence number.
     */
    public int nextSeqNumber() {
        return sendSeqNumber++;
    }
}
