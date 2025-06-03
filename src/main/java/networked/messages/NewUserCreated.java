package networked.messages;

import networked.UserInfo;

public class NewUserCreated extends NetworkedMessage{
    private UserInfo userInfo;

    public NewUserCreated() {}
    public NewUserCreated(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
