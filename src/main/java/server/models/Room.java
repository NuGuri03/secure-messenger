package server.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import server.Database.DatabaseManager;

public class Room extends Model {
    private long id;
    private String name;

    private Room(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws SQLException {
        this.name = name;
        save();
    }

    public void destroy() {
        var db = DatabaseManager.getInstance();

        db.query("DELETE FROM rooms WHERE id = ?", stmt -> {
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        });
    }

    public RoomUser[] getUsers() {
        return RoomUser.queryByRoom(this);
    }

    public void addUser(User user, byte[] encryptedKey) {
        var db = DatabaseManager.getInstance();

        db.query("INSERT INTO room_users (room_id, user_id, encrypted_key) VALUES (?, ?, ?)", stmt -> {
            stmt.setLong(1, id);
            stmt.setLong(2, user.getId());
            stmt.setBytes(3, encryptedKey);
            return stmt.executeUpdate();
        });
    }

    public boolean isUserInRoom(User user) {
        var db = DatabaseManager.getInstance();

        return db.query("SELECT COUNT(*) FROM room_users WHERE room_id = ? AND user_id = ?", stmt -> {
            stmt.setLong(1, id);
            stmt.setLong(2, user.getId());

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        });
    }

    public Message[] getRecentMessages(int limit, long beforeTimestamp) {
        return Message.queryByRoom(this, limit, beforeTimestamp);
    }

    public Message addMessage(User author, byte[] encryptedContent) throws SQLException {
        return Message.create(this, author, encryptedContent);
    }

    @Override
    public String toString() {
        return "[Room " + id + ": '" + name + "'']";
    }


    public static Room queryById(long id) {
        var db = DatabaseManager.getInstance();
        return db.query("SELECT * FROM rooms WHERE id = ?", stmt -> {
            stmt.setLong(1, id);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next()) { return null; }
                return fromResultSet(rs);
            }
        });
    }

    public static Room[] queryAll() {
        var db = DatabaseManager.getInstance();
        return db.query("SELECT * FROM rooms ORDER BY name", stmt -> {
            var rooms = new ArrayList<Room>();

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(fromResultSet(resultSet));
                }
            }

            return rooms.toArray(new Room[0]);
        });
    }

    public static Room create(String name) throws SQLException {
        var db = DatabaseManager.getInstance();

        long id = db.generateId();
        Room room = new Room(id, name);
        room.save();

        return room;
    }


    private void save() throws SQLException {
        var db = DatabaseManager.getInstance();

        db.query("INSERT OR REPLACE INTO rooms (id, name) VALUES (?, ?)", stmt -> {
            stmt.setLong(1, id);
            stmt.setString(2, name);
            return stmt.executeUpdate();
        });
    }

    private static Room fromResultSet(ResultSet rs) throws SQLException {
        return new Room(
            rs.getLong("id"),
            rs.getString("name")
        );
    }
}
