package networked;


public class UserInfo {
    private long id;
    private String  handle;
    private String  username;
    private String  bio;
    private String  avatarPath;
    private byte[]  publicKey;

    public UserInfo() {}

    public UserInfo(long id, String handle, String username, String bio,
                    String avatarPath, byte[] publicKey) {
        this.id         = id;
        this.handle     = handle;
        this.username   = username;
        this.bio        = bio;
        this.avatarPath = avatarPath;
        this.publicKey  = publicKey;
    }

    /* ------------------- getters ---------------------------- */
    public long    getId()          { return id;       }
    public String  getHandle()     { return handle;   }
    public String  getUsername()   { return username; }
    public String  getBio()        { return bio;      }
    public String  getAvatarPath() { return avatarPath; }
    public byte[]  getPublicKey()  { return publicKey; }

    /* ------------------- setters ---------------------------- */
    public void setId(long id)                  { this.id = id; }
    public void setHandle(String h)          { this.handle = h; }
    public void setUsername(String u)        { this.username = u; }
    public void setBio(String b)             { this.bio = b; }
    public void setAvatarPath(String path)   { this.avatarPath = path; }
    public void setPublicKey(byte[] pkBytes) { this.publicKey = pkBytes; }
}
