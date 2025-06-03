package networked;

import java.io.Serializable;

public class RoomDTO  implements Serializable {
    public long roomId;
    public String roomName;
    public byte[] encKey;            // cifrada para el receptor
    public String[] memberHandles;
    public MsgDTO[] messages;
}
