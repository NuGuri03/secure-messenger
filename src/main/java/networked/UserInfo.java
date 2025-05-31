package networked;

import java.security.PublicKey;

/**
 * @brief 사용자 정보를 저장하는 클래스.
 */
public class UserInfo {
    private String handle;
    private String username;
    private String bio;
    private String avatarPath;
    private PublicKey publicKey;

    public UserInfo(String handle, String username, String bio, String avatarPath, PublicKey publicKey) {
        this.handle = handle;
        this.username = username;
        this.bio = bio;
        this.avatarPath = avatarPath;
        this.publicKey = publicKey;
    }

    /*--------------getter method--------------*/

    public String getHandle() {
        return handle;
    }

    public String getUsername() {
        return username;
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

    /*-----------setter method-----------*/
    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
