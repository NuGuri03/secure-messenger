package server.database;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import server.models.*;

public class DatabaseTest {
    public static void main(String[] args) throws Exception {
        final String testDatabaseName = "database_test.db";

        if (Files.exists(new File(testDatabaseName).toPath())) {
            Files.delete(new File(testDatabaseName).toPath());
            print("Existing test database file removed.");
        } else {
            print("No existing test database file found.");
        }

        DatabaseManager.init(testDatabaseName);
        DatabaseManager db = DatabaseManager.getInstance();

        print("Database initialized. Starting tests...");
        print("");

        testUser(db);
        print("");

        testRoom(db);

        print("All tests completed successfully!");
    }

    public static void testUser(DatabaseManager db) throws Exception {
        byte[] publicKey = new byte[]{1, 2, 3, 4, 5};
        byte[] encryptedPrivateKey = new byte[]{6, 7, 8, 9, 10};
        byte[] encryptedPrivateKeyIv = new byte[]{11, 12, 13, 14, 15};
        byte[] authenticationKey = new byte[]{16, 17, 18, 19, 20};

        // Check if the user handle already exists
        assert !User.isHandleExists("testuser") : "Handle 'testuser' should not exist before creation";

        // Create a new user using UserBuilder
        User user = new UserBuilder()
            .setHandle("TestUser")
            .setPublicKey(publicKey)
            .setNickname("Test User")
            .setEncryptedPrivateKey(encryptedPrivateKey)
            .setEncryptedPrivateKeyIv(encryptedPrivateKeyIv)
            .setAuthenticationKey(authenticationKey)
            .createUser();

        print("User saved: " + user);

        // Verify the user handle exists now
        assert User.isHandleExists("TestUser");
        assert User.isHandleExists("testuser");
        assert User.isHandleExists("TESTUsER");

        // Query the user by handle (case-insensitive)
        User queriedUser = User.queryByHandle("testuser");
        print("Should be quried with lowercase handle: " + queriedUser);

        assert queriedUser != null;
        assert equals(queriedUser.getId(), user.getId());
        assert equals(queriedUser.getHandle(), "TestUser");
        assert equals(queriedUser.getNickname(), "Test User");
        assert equals(queriedUser.getPublicKey(), publicKey);
        assert equals(queriedUser.getEncryptedPrivateKey(), encryptedPrivateKey);
        assert equals(queriedUser.getEncryptedPrivateKeyIv(), encryptedPrivateKeyIv);
        assert equals(queriedUser.getBio(), "");
        assert queriedUser.verifyAuthenticationKey(authenticationKey);

        // Update the user's nickname
        queriedUser.setNickname("Updated User");
        print("User nickname updated to: " + queriedUser.getNickname());
        assert equals(User.queryByHandle("testuser").getNickname(), "Updated User") : "Nickname should be updated";
        
        // Update the user's bio
        queriedUser.setBio("This is a test bio.");
        print("User bio updated to: " + queriedUser.getBio());
        assert equals(User.queryByHandle("testuser").getBio(), "This is a test bio.") : "Bio should be updated";

        // Create another user
        int usersToCreate = 10;
        for (int i = 0; i < usersToCreate; i++) {
            User anotherUser = new UserBuilder()
                .setHandle("anotheruser" + i)
                .setPublicKey(new byte[]{1, 2, 3, 4, 5, (byte) i})
                .setNickname("Another User " + i)
                .setEncryptedPrivateKey(new byte[]{6, 7, 8, 9, 10, (byte) i})
                .setEncryptedPrivateKeyIv(new byte[]{11, 12, 13, 14, 15, (byte) i})
                .setAuthenticationKey(new byte[]{16, 17, 18, 19, 20, (byte) i})
                .createUser();
            print("Another user created: " + anotherUser.getHandle());
        }

        // Query all users
        User[] allUsers = User.queryAll();
        print("All users in the database:");
        for (User u : allUsers) {
            print(" - " + u);
        }

        assert allUsers.length == usersToCreate + 1;
        assert Arrays.stream(allUsers).anyMatch(u -> u.getHandle().equals("testuser"));
        assert Arrays.stream(allUsers).anyMatch(u -> u.getHandle().equals("anotheruser0"));
        assert Arrays.stream(allUsers).anyMatch(u -> u.getHandle().equals("anotheruser" + (usersToCreate - 1)));

        for (int i = 0; i < usersToCreate; i++) {
            assert User.isHandleExists("anotheruser" + i);
        }

        // Verify that invalid handles are thrown
        var userBuilder = new UserBuilder()
            .setPublicKey(new byte[]{1, 2, 3, 4, 5})
            .setNickname("Invalid User")
            .setEncryptedPrivateKey(new byte[]{6, 7, 8, 9, 10})
            .setEncryptedPrivateKeyIv(new byte[]{11, 12, 13, 14, 15})
            .setAuthenticationKey(new byte[]{16, 17, 18, 19, 20});
        
        userBuilder.setHandle("valiD.user_Handle1");
        userBuilder.createUser(); // Should succeed
        print("User with valid handle created: " + "valiD.user_Handle1");

        testInvalidUserHandle(userBuilder, "s");
        testInvalidUserHandle(userBuilder, "l".repeat(33));
        testInvalidUserHandle(userBuilder, "한글이름");
        testInvalidUserHandle(userBuilder, "jAVAM@STER");
        testInvalidUserHandle(userBuilder, "space in handle");
        testInvalidUserHandle(userBuilder, "  untrimmed_name  ");

        print("User: test completed successfully!");

        // Clean up the database
        db.query("DELETE FROM users", s -> null);
    }

    private static void testInvalidUserHandle(UserBuilder userBuilder, String handle) throws Exception {
        print("Testing if invalid user handle rejects: " + handle);
        assert !User.isHandleValid(handle);

        try {
            userBuilder.setHandle(handle).createUser();
            assert false : "Should not allow handle with less than 4 characters";
        } catch (IllegalArgumentException e) {}
    }

    public static void testRoom(DatabaseManager db) throws Exception {
        // Create a user
        User user = new UserBuilder()
            .setHandle("roomuser")
            .setPublicKey(new byte[]{1, 2, 3, 4, 5})
            .setNickname("Room User")
            .setEncryptedPrivateKey(new byte[]{6, 7, 8, 9, 10})
            .setEncryptedPrivateKeyIv(new byte[]{11, 12, 13, 14, 15})
            .setAuthenticationKey(new byte[]{16, 17, 18, 19, 20})
            .createUser();

        // Create a room
        Room room = Room.create("Test Room");
        print("Room.create(): " + room);

        Room room2 = Room.create("Another Room");
        print("Room.create(): " + room2);

        // Query the room by ID
        Room queriedRoom = Room.queryById(room.getId());
        print("Room.queryById(" + room.getId() + "): " + queriedRoom);

        assert queriedRoom != null;
        assert queriedRoom.getName().equals("Test Room");
    
        // Query all rooms
        Room[] allRooms = Room.queryAll();
        print("Room.queryAll():");
        for (Room r : allRooms) {
            print(" - " + r.getName() + " (ID: " + r.getId() + ")");
        }

        assert allRooms.length == 2;
        
        // Check that the user is not in the room
        print("room.isUserInRoom(" + user + "): " + room.isUserInRoom(user));
        assert !room.isUserInRoom(user) : "User should not be in the room initially";

        // Add the user to the room
        var encryptedKeyForUser = new byte[]{21, 22, 23, 24};
        room.addUser(user, encryptedKeyForUser);
        print("room.addUser(" + user + ")");

        // Check if the user is in the room
        print("room.isUserInRoom(" + user + "): " + room.isUserInRoom(user));
        assert room.isUserInRoom(user) : "User should be in the room now";

        // Add 15 messages to the room
        for (int i = 0; i < 15; i++) {
            Message message = Message.create(room, user, new byte[]{(byte) i, (byte) i, (byte) i});
            print("Message.create(room, user, bytes): " + Arrays.toString(message.getEncryptedContent()));
        }

        // Get recent messages (1)
        Message[] recentMessages = room.getRecentMessages(10, System.currentTimeMillis());

        print("room.getRecentMessages(10, ms) (1 of 2):");
        for (Message m : recentMessages) {
            print(" - " + Arrays.toString(m.getEncryptedContent()));
        }
        assert recentMessages.length == 10;

        // Get recent messages (2)
        var mostOldMessageFromFirstQuery = recentMessages[recentMessages.length - 1];
        recentMessages = room.getRecentMessages(10, mostOldMessageFromFirstQuery.getCreatedAt());

        print("room.getRecentMessages(10, ms) (2 of 2):");
        for (Message m : recentMessages) {
            print(" - " + Arrays.toString(m.getEncryptedContent()));
        }
        assert recentMessages.length == 5;

        // Clean up the database
        db.query("DELETE FROM rooms", stmt -> null);
    }

    private static void print(String message) {
        if (message.isEmpty()) {
            System.out.println("");
        } else {
            System.out.println("[Test] " + message);
        }
    }

    private static boolean equals(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    private static boolean equals(String a, String b) {
        return a.equals(b);
    }
    
    private static boolean equals(long a, long b) {
        return a == b;
    }
}
