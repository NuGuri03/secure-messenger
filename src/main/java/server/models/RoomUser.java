package server.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import server.Database.DatabaseManager;

public class RoomUser extends Model {
    private long roomId;
    private long userId;
    private byte[] encryptedKey;

    private RoomUser(long roomId, long userId, byte[] encryptedKey) {
        this.roomId = roomId;
        this.userId = userId;
        this.encryptedKey = encryptedKey;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getUserId() {
        return userId;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public Room getRoom() {
        return Room.queryById(roomId);
    }

    public User getUser() {
        return User.queryById(userId);
    }

    public String getRoomName() {
        return getRoom().getName();
    }


    public static RoomUser[] queryByUser(User user) {
        var db = DatabaseManager.getInstance();

        return db.query("SELECT * FROM room_users WHERE user_id = ?", stmt -> {
            stmt.setLong(1, user.getId());

            var roomUsers = new ArrayList<RoomUser>();
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    roomUsers.add(RoomUser.fromResultSet(resultSet));
                }
            }

            return roomUsers.toArray(new RoomUser[0]);
        });
    }

    public static RoomUser[] queryByRoom(Room room) {
        var db = DatabaseManager.getInstance();

        return db.query("SELECT * FROM room_users WHERE room_id = ?", stmt -> {
            stmt.setLong(1, room.getId());

            var roomUsers = new ArrayList<RoomUser>();
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    roomUsers.add(RoomUser.fromResultSet(resultSet));
                }
            }

            return roomUsers.toArray(new RoomUser[0]);
        });
    }

    @Override
    public String toString() {
        return "[RoomUser: room " + roomId + " - user " + userId + "]";
    }


    private static RoomUser fromResultSet(ResultSet rs) throws SQLException {
        return new RoomUser(
            rs.getLong("room_id"),
            rs.getLong("user_id"),
            rs.getBytes("encrypted_key")
        );
    }
}
