package networked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomInfo {
    private long id;
    private String name;
    private byte[] encryptionKey;
    private String[] memberHandles;

    private final List<Message> messages = new ArrayList<>();

    // Only meaningful here
    public record Message(
        long   id,
        String authorHandle,
        long   createdAt,
        String plainText
    ) {}

    public RoomInfo() {}

    public RoomInfo(long id, String name, byte[] encryptionKey, String[] memberHandles) {
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

    public byte[] getEncryptionKey() { return encryptionKey; }
    public void   setEncryptionKey(byte[] key) { this.encryptionKey = key; }

    public String[] getMemberHandles() {
        return memberHandles;
    }

    public List<Message> getMessages()  { return Collections.unmodifiableList(messages); }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemberHandles(String[] memberHandles) {
        this.memberHandles = memberHandles;
    }

    public void addMessage(Message m) { messages.add(m); }

    public String getLastMessage() {
        if (messages.isEmpty()) {
            return "";
        }
        Message lastMessage = messages.get(messages.size() - 1);
        return lastMessage.plainText();
    }

    public long getLastMessageTimestamp() {
        return messages.isEmpty() ? 0L : messages.get(messages.size() - 1).createdAt();
    }

}
