package server.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import server.database.DatabaseManager;

public class Message extends Model {
    private long id;
    private long roomId;
    private long authorId;
    private byte[] encryptedContent;
    private long createdAt;

    private Message(long id, long roomId, long authorId, byte[] encryptedContent, long createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.authorId = authorId;
        this.encryptedContent = encryptedContent;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getAuthorId() {
        return authorId;
    }

    public byte[] getEncryptedContent() {
        return encryptedContent;
    }

    public long getCreatedAt() {
        return createdAt;
    }


    public static Message create(Room room, User author, byte[] encryptedContent) throws SQLException {
        var db = DatabaseManager.getInstance();

        long id = db.generateId();
        long createdAt = System.currentTimeMillis();

        Message message = new Message(id, room.getId(), author.getId(), encryptedContent, createdAt);
        message.insert();

        return message;
    }

    public static Message[] queryByRoom(Room room, int limit, long beforeTimestamp) {
        var db = DatabaseManager.getInstance();

        return db.query("SELECT * FROM messages WHERE room_id = ? AND created_at < ? ORDER BY created_at ASC LIMIT ?", stmt -> {
            stmt.setLong(1, room.getId());
            stmt.setLong(2, beforeTimestamp);
            stmt.setInt(3, limit);

            try (var rs = stmt.executeQuery()) {
                var messages = new ArrayList<Message>();
                while (rs.next()) {
                    messages.add(Message.fromResultSet(rs));
                }
                return messages.toArray(new Message[0]);
            }
        });
    }

    @Override
    public String toString() {
        return "[Message " + id + ": roomId: " + roomId + ", authorId: " + authorId + "]";
    }


    private void insert() throws SQLException {
        var db = DatabaseManager.getInstance();

        db.query("INSERT INTO messages (id, room_id, author_id, encrypted_content, created_at) VALUES (?, ?, ?, ?, ?)", stmt -> {
            stmt.setLong(1, id);
            stmt.setLong(2, roomId);
            stmt.setLong(3, authorId);
            stmt.setBytes(4, encryptedContent);
            stmt.setLong(5, createdAt);
            return stmt.executeUpdate();
        });
    }

    private static Message fromResultSet(ResultSet rs) throws SQLException {
        return new Message(
            rs.getLong("id"),
            rs.getLong("room_id"),
            rs.getLong("author_id"),
            rs.getBytes("encrypted_content"),
            rs.getLong("created_at")
        );
    }
}
