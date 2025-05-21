package client;

import javax.swing.SwingUtilities;

import client.ui.*;
import networked.RoomInfo;

public class WindowManager {
    private static BaseUI currentUI;
    private static ChatClient client;
    
    public static void start(ChatClient chatClient) {
        client = chatClient;
        currentUI = new LoginUI(client);
    }

    public static void toLoginUI() {
        var ui = currentUI;
        currentUI = new LoginUI(client);
        ui.dispose();
    }

    public static void toMainUI() {
        var ui = currentUI;
        currentUI = new MainUI(client);
        ui.dispose();
    }

    public static void openSignUpUI() {
        // 새 창을 띄우되, 기존 창과는 상호작용하지 못하게 한다.
        var currentUI = WindowManager.currentUI;
        currentUI.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpUI = new SignUpUI(client);
            signUpUI.setVisible(true);

            signUpUI.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    currentUI.setEnabled(true);
                }
            });
        });
    }

    public static void openChatUI(RoomInfo roomInfo) {
        SwingUtilities.invokeLater(() -> {
            ChatUI chatUI = new ChatUI(client, roomInfo);
            chatUI.setVisible(true);
        });
    }
}
