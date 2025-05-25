package server.Database;

import java.sql.SQLException;

import server.models.User;

public class UserBuilder {
    private String handle;
    private byte[] publicKey;
    private String nickname;
    private String bio = null;
    private byte[] encryptedPrivateKey;
    private byte[] encryptedPrivateKeyIv;
    private byte[] authenticationKey;

    public UserBuilder() {}

    public UserBuilder setHandle(String handle) {
        this.handle = handle;
        return this;
    }

    public UserBuilder setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public UserBuilder setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserBuilder setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public UserBuilder setEncryptedPrivateKey(byte[] encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
        return this;
    }

    public UserBuilder setEncryptedPrivateKeyIv(byte[] encryptedPrivateKeyIv) {
        this.encryptedPrivateKeyIv = encryptedPrivateKeyIv;
        return this;
    }

    public UserBuilder setAuthenticationKey(byte[] authenticationKey) {
        this.authenticationKey = authenticationKey;
        return this;
    }

    public User createUser() throws SQLException {
        assert handle != null : "Handle must not be null";
        assert publicKey != null : "Public key must not be null";
        assert nickname != null : "Nickname must not be null";
        assert encryptedPrivateKey != null : "Encrypted private key must not be null";
        assert encryptedPrivateKeyIv != null : "Encrypted private key IV must not be null";
        assert authenticationKey != null : "Authentication key must not be null";

        bio = (bio != null) ? bio : "";
        return User.create(handle, publicKey, nickname, bio,
                           encryptedPrivateKey, encryptedPrivateKeyIv, 
                           authenticationKey);
    }
}
