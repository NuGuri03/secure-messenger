package server.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import networked.CryptoUtil;
import server.database.DatabaseManager;

public class User extends Model {
    private long id;
    private String handle;
    private byte[] publicKey;
    private String nickname;
    private String bio;
    private byte[] encryptedPrivateKey;
    private byte[] encryptedPrivateKeyIv;
    private byte[] hashedAuthenticationKey;
    private byte[] hashedAuthenticationKeySalt;
    private long createdAt;
    private long updatedAt;

    private User(long id, String handle, byte[] publicKey, String nickname, String bio,
                 byte[] encryptedPrivateKey, byte[] encryptedPrivateKeyIv, 
                 byte[] hashedAuthenticationKey, byte[] hashedAuthenticationKeySalt,
                 long createdAt, long updatedAt) {
        this.id = id;
        this.handle = handle;
        this.publicKey = publicKey;
        this.nickname = nickname;
        this.bio = bio;
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.encryptedPrivateKeyIv = encryptedPrivateKeyIv;
        this.hashedAuthenticationKey = hashedAuthenticationKey;
        this.hashedAuthenticationKeySalt = hashedAuthenticationKeySalt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) throws SQLException {
        this.nickname = nickname.trim();
        save();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) throws SQLException {
        this.bio = (bio != null) ? bio.trim() : "";
        save();
    }

    public byte[] getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public byte[] getEncryptedPrivateKeyIv() {
        return encryptedPrivateKeyIv;
    }

    public boolean verifyAuthenticationKey(byte[] authenticationKey) {
        byte[] hashedKey = CryptoUtil.kdf(authenticationKey, hashedAuthenticationKeySalt);
        return CryptoUtil.secureCompareBytes(hashedKey, hashedAuthenticationKey);
    }

    public RoomUser[] getRooms() {
        return RoomUser.queryByUser(this);
    }

    @Override
    public String toString() {
        return "[User " + id + ": '" + nickname + "' (@" + handle + ")]";
    }


    public static User create(String handle, byte[] publicKey, String nickname, String bio,
                              byte[] encryptedPrivateKey, byte[] encryptedPrivateKeyIv,
                              byte[] authenticationKey) throws SQLException {
        if (!isHandleValid(handle)) {
            throw new IllegalArgumentException("Invalid handle format");
        }

        var db = DatabaseManager.getInstance();

        long id = db.generateId();
        byte[] hashedAuthenticationKeySalt = CryptoUtil.generateRandomBytes(16);
        byte[] hashedAuthenticationKey = CryptoUtil.kdf(authenticationKey, hashedAuthenticationKeySalt);
        long createdAt = System.currentTimeMillis();
        long updatedAt = createdAt;

        User user = new User(id, handle, publicKey, nickname, "", 
                             encryptedPrivateKey, encryptedPrivateKeyIv, 
                             hashedAuthenticationKey, hashedAuthenticationKeySalt,
                             createdAt, updatedAt);
        user.save();
        return user;
    }

    public static User queryById(long id) {
        var db = DatabaseManager.getInstance();
        return db.query("SELECT * FROM users WHERE id = ?", stmt -> {
            stmt.setLong(1, id);

            var result = stmt.executeQuery();
            if (!result.next()) { return null; }
            return fromResultSet(result);
        });
    }

    public static User queryByHandle(String handle) {
        var db = DatabaseManager.getInstance();
        return db.query("SELECT * FROM users WHERE handle = ?", stmt -> {
            stmt.setString(1, handle);

            var result = stmt.executeQuery();
            if (!result.next()) { return null; }
            return fromResultSet(result);
        });
    }

    public static User[] queryAll() {
        var db = DatabaseManager.getInstance();
        return db.query("SELECT * FROM users ORDER BY nickname", stmt -> {
            var users = new ArrayList<User>();

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    users.add(fromResultSet(resultSet));
                }
            }

            return users.toArray(new User[0]);
        });
    }

    public static boolean isHandleExists(String handle) {
        var db = DatabaseManager.getInstance();

        return db.query("SELECT COUNT(*) FROM users WHERE handle = ?", stmt -> {
            stmt.setString(1, handle);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        });
    }

    public static boolean isHandleValid(String handle) {
        if (handle == null || handle.isEmpty()) {
            return false;
        }

        // 핸들은 4-32자 사이의 영어 대소문자, 숫자, 밑줄(_), 대시(-), 온점(.) 만 허용
        return handle.matches("[a-zA-Z0-9_.\\-]{4,32}");
    }


    private void save() throws SQLException {
        var db = DatabaseManager.getInstance();

        updatedAt = System.currentTimeMillis();
        db.query("INSERT OR REPLACE INTO users (id, handle, public_key, nickname, bio, " +
                 "encrypted_private_key, encrypted_private_key_iv, hashed_authentication_key, " +
                 "hashed_authentication_key_salt, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", stmt -> {
            stmt.setLong(1, id);
            stmt.setString(2, handle);
            stmt.setBytes(3, publicKey);
            stmt.setString(4, nickname);
            stmt.setString(5, bio);
            stmt.setBytes(6, encryptedPrivateKey);
            stmt.setBytes(7, encryptedPrivateKeyIv);
            stmt.setBytes(8, hashedAuthenticationKey);
            stmt.setBytes(9, hashedAuthenticationKeySalt);
            stmt.setLong(10, createdAt);
            stmt.setLong(11, updatedAt);

            return stmt.executeUpdate();
        });
    }

    private static User fromResultSet(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getLong("id"),
            resultSet.getString("handle"),
            resultSet.getBytes("public_key"),
            resultSet.getString("nickname"),
            resultSet.getString("bio"),
            resultSet.getBytes("encrypted_private_key"),
            resultSet.getBytes("encrypted_private_key_iv"),
            resultSet.getBytes("hashed_authentication_key"),
            resultSet.getBytes("hashed_authentication_key_salt"),
            resultSet.getLong("created_at"),
            resultSet.getLong("updated_at")
        );
    }
}
