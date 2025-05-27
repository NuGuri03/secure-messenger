package server;

import java.io.IOException;

import server.database.DatabaseManager;

public class ServerMain {
    public static void main(String[] args) {
        try {
            DatabaseManager.init();
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
