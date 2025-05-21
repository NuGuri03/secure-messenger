package common;

import java.security.PublicKey;

/**
 * @brief 사용자 정보를 저장하는 클래스.
 */
public class UserInfo {
    private String handle;
    private String nickname;
    private String bio;
    private PublicKey publicKey;

    public UserInfo(String handle, String nickname, String bio, PublicKey publicKey) {
        this.handle = handle;
        this.nickname = nickname;
        this.bio = bio;
        this.publicKey = publicKey;
    }

    public String getHandle() {
        return handle;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
