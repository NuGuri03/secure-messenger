package networked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import networked.messages.*;

public class MessageTypeIndex {
    public static List<Class<?>> getAllMessageTypes() {
        var messages = new ArrayList<Class<?>>();

        messages.add(byte[].class);
        messages.add(byte[][].class);
        messages.add(EncryptedMessage.class);
        messages.add(KeyExchangeMessage.class);

        messages.add(SessionHelloMessage.class);
        messages.add(RegisterRequest.class);
        messages.add(RegisterResponse.class);
        messages.add(LoginRequest.class);
        messages.add(LoginResponse.class);
        messages.add(AllUserInfoRequest.class);
        messages.add(LoginChallenge.class);
        messages.add(LoginChallengeResponse.class);

        messages.add(UserInfo.class);
        messages.add(UserInfo[].class);

        messages.add(RoomInfo.class);
        messages.add(RoomsMessage.class);
        messages.add(HashMap.class);
        messages.add(String[].class);
        messages.add(List.class);
        messages.add(ArrayList.class);

        messages.add(AllUserInfoResponse.class);

        messages.add(CreateRoom.class);
        messages.add(RequestCreateRoom.class);
        messages.add(RoomDTO.class);
        messages.add(ReceivedMessage.class);
        messages.add(MsgDTO.class);
        messages.add(RoomDTO[].class);
        messages.add(MsgDTO[].class);
        messages.add(SendMessage.class);
        messages.add(RoomList.class);
        messages.add(RequestRoomList.class);
        messages.add(NewUserCreated.class);

        messages.add(UserInfoChanged.class);
        messages.add(UserInfoChangeRequest.class);

        return messages;
    }
}
