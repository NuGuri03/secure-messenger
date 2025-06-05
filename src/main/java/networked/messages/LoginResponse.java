package networked.messages;

import networked.UserInfo;

public class LoginResponse extends NetworkedMessage {
    public boolean success;
    public UserInfo userInfo;
}
