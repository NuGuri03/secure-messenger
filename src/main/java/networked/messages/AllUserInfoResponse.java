package networked.messages;

import networked.UserInfo;

public class AllUserInfoResponse extends NetworkedMessage {
    private UserInfo[] userInfos;

    public AllUserInfoResponse() {}

    public AllUserInfoResponse(UserInfo[] userInfos) {
        this.userInfos = userInfos;
    }

    public UserInfo[] getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(UserInfo[] userInfos) {
        this.userInfos = userInfos;
    }
}
