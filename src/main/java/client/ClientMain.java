package client;

import java.io.IOException;

import javax.swing.JOptionPane;

public class ClientMain {
    public static void main(String[] args) {
        try {
            ServerInfo serverInfo = ServerInfo.readConfiguration();
            ChatClient chatClient = new ChatClient(serverInfo);
            chatClient.connect();
            WindowManager.start(chatClient);
        } catch (IOException e) {
            e.printStackTrace();
            displayErrorMessage("서버에 연결할 수 없습니다. 서버 주소와 포트를 확인하세요.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void displayErrorMessage(String message) {
        System.err.println("Initialization Failed: " + message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}	