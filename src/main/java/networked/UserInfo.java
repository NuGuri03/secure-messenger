package networked;

import java.security.PublicKey;

/**
 * @brief 사용자 정보를 저장하는 클래스.
 */
public class UserInfo {
    private String handle;
    private String nickname;
    private String bio;
    private String avatarPath;
    private PublicKey publicKey;

    public UserInfo(String handle, String nickname, String bio, String avatarPath, PublicKey publicKey) {
        this.handle = handle;
        this.nickname = nickname;
        this.bio = bio;
        this.avatarPath = avatarPath;
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

    public String getAvatarPath() {
        return avatarPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
