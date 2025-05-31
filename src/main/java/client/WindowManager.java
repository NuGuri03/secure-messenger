package client;

import javax.swing.*;

import client.ui.*;
import client.ui.panel.LobbyPanel;
import networked.RoomInfo;
import networked.UserInfo;

import java.awt.*;

public class WindowManager {
    private static BaseUI currentUI;
    private static ChatClient client;

    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    private static LobbyPanel lobbyPanel;

    public static void start(ChatClient chatClient) {
        client = chatClient;
        currentUI = new LoginUI(client);
    }

    public static void toLoginUI() {
        var ui = currentUI;
        currentUI = new LoginUI(client);
        ui.dispose();
    }

    public static void openSignUpUI() {
        // 새 창을 띄우되, 기존 창과는 상호작용하지 못하게 한다.
        var currentUI = WindowManager.currentUI;
        currentUI.setFormEnabled(false);

        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpUI = new SignUpUI(client);
            signUpUI.setVisible(true);

            signUpUI.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    currentUI.setFormEnabled(true);
                }
            });
        });
    }

    public static void toMainUI() {
        var ui = currentUI;
        currentUI = new MainUI(client);
        ui.dispose();
    }

    public static void initMainPanel(JPanel panel) {
        mainPanel = panel;
        cardLayout = (CardLayout) panel.getLayout();
        lobbyPanel = new LobbyPanel(client);
    }

    public static void showLobby() {
        SwingUtilities.invokeLater(() -> {
            // 기존 것을 통째로 제거하고
            mainPanel.remove(lobbyPanel);
            // 새로 생성
            lobbyPanel = new LobbyPanel(client);
            mainPanel.add(lobbyPanel, "lobby");
            cardLayout.show(mainPanel, "lobby");
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    public static void showChat() {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "chat"));
    }

    public static void showSettings() {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "settings"));
    }

    public static void openChatUI(RoomInfo roomInfo) {
        SwingUtilities.invokeLater(() -> {
            ChatUI chatUI = new ChatUI(client, roomInfo);
            chatUI.setVisible(true);
        });
    }

    public static void openProfileUI(UserInfo userInfo) {
        SwingUtilities.invokeLater(() -> {
            ProfileUI profileUI = new ProfileUI(client, userInfo);
            profileUI.setVisible(true);
        });
    }
}
