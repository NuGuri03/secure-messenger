package client;

import javax.swing.*;

import client.ui.*;
import client.ui.panel.LobbyPanel;
import client.ui.panel.RecentChatPanel;
import networked.RoomInfo;
import networked.UserInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WindowManager {
    private static WindowManager instance;

    private static BaseUI currentUI;
    private static ChatClient client;

    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    public static LobbyPanel lobbyPanel;
    public static RecentChatPanel recentChatPanel;

    private static final List<ChatUI> chatUIs = new ArrayList<ChatUI>();
    public static CurrentUIState state;

    private static TrayIcon trayIcon;


    //singleton pattern
    public static  WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }


    public static void start(ChatClient chatClient) {
        client = chatClient;
        currentUI = new LoginUI(client);
        state = CurrentUIState.LOGIN;

        // 시스템 트레이 아이콘 설정
        if (SystemTray.isSupported()) {
            var systemTray = SystemTray.getSystemTray();
            try {
                Image image = ResourceCache.getIcon("/icons/logo.png", 64).getImage();
                trayIcon = new TrayIcon(image, "Chat Client");
                trayIcon.setImageAutoSize(true);
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("[WindowManager] System Tray is not supported on this platform.");
        }
    }

    public static void toLoginUI() {
        var ui = currentUI;
        currentUI = new LoginUI(client);
        state = CurrentUIState.LOGIN;
        ui.dispose();
    }

    public static void openSignUpUI() {
        // 새 창을 띄우되, 기존 창과는 상호작용하지 못하게 한다.
        var currentUI = WindowManager.currentUI;
        currentUI.setFormEnabled(false);

        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpUI = new SignUpUI(client);
            state = CurrentUIState.LOGIN;
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
        state = CurrentUIState.LOBBY;
    }

    public static void initMainPanel(JPanel panel) {
        mainPanel = panel;
        cardLayout = (CardLayout) panel.getLayout();
        lobbyPanel = new LobbyPanel(client);
        recentChatPanel = new RecentChatPanel(client);
    }

    public static void showLobby() {
        SwingUtilities.invokeLater(() -> {
            mainPanel.remove(lobbyPanel);
            lobbyPanel = new LobbyPanel(client);
            mainPanel.add(lobbyPanel, "lobby");
            cardLayout.show(mainPanel, "lobby");
            mainPanel.revalidate();
            mainPanel.repaint();
            state = CurrentUIState.LOBBY;
        });
    }

    public static void showChat() {
        SwingUtilities.invokeLater(() -> {
            mainPanel.remove(recentChatPanel);
            recentChatPanel = new RecentChatPanel(client);
            mainPanel.add(recentChatPanel, "chat");
            cardLayout.show(mainPanel, "chat");
            mainPanel.revalidate();
            mainPanel.repaint();
            state = CurrentUIState.RECENT;
        });
    }

    public static void showSettings() {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "settings"));
        state = CurrentUIState.SETTINGS;
    }

    public static void openChatUI(RoomInfo roomInfo) {
        SwingUtilities.invokeLater(() -> {
            var chatUI = new ChatUI(client, roomInfo);
            chatUI.setVisible(true);
            chatUIs.add(chatUI);

            chatUI.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    chatUIs.remove(chatUI);
                }
            });
        });
    }

    public static void openProfileUI(UserInfo userInfo) {
        SwingUtilities.invokeLater(() -> {
            ProfileUI profileUI = new ProfileUI(client, userInfo);
            profileUI.setVisible(true);
        });
    }

    public static void showIncomingMessage(RoomInfo.Message m)
    {
        for (ChatUI chatUI : chatUIs) {
            if (chatUI.roomInfo.getId() == m.id()) {
                chatUI.appendIncoming(m);
            }
        }

        UserInfo sender = client.findUser(m.authorHandle());
        boolean isMyChat = m.authorHandle().equals(client.getUserInfo().getHandle());

        if (trayIcon != null && !isMyChat) {
            String senderName = sender != null ? sender.getUsername() : m.authorHandle();
            trayIcon.displayMessage(senderName, senderName + ": " + m.plainText(), TrayIcon.MessageType.INFO);
        }
    }
}
