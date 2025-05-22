package client;

import client.ui.ChatUI;
import client.ui.LoginUI;

public class ClientMain {
    public static void main(String[] args) {
//        new LoginUI();
        ChatUI chat = new ChatUI("client");
        chat.setVisible(true);
    }
}