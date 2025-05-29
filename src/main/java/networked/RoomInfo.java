package networked;

import javax.crypto.SecretKey;

public class RoomInfo {
    private long id;
    private String name;
    private SecretKey encryptionKey;
    private String[] memberHandles;

    public RoomInfo(long id, String name, SecretKey encryptionKey, String[] memberHandles) {
        this.id = id;
        this.name = name;
        this.encryptionKey = encryptionKey;
        this.memberHandles = memberHandles;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }

    public String[] getMemberHandles() {
        return memberHandles;
    }
}
