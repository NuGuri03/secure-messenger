package networked;

import java.io.Serializable;

public class MsgDTO implements Serializable {
    public long id;
    public long authorId;
    public long  createdAt;
    public byte[] encryptedContent;
}
