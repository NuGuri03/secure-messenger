package networked.messages;

import networked.RoomInfo;
import networked.UserInfo;

import java.util.HashMap;
import java.util.List;

public class RoomsMessage extends NetworkedMessage{
    private HashMap<RoomInfo, List<UserInfo>> rooms = new HashMap<>();

    public RoomsMessage() {}

    public RoomsMessage(HashMap<RoomInfo, List<UserInfo>> rooms) {
        this.rooms = rooms;
    }

    public HashMap<RoomInfo, List<UserInfo>> getRooms() {
        return rooms;
    }

    public void setRooms(HashMap<RoomInfo, List<UserInfo>> rooms) {
        this.rooms = rooms;
    }
}
