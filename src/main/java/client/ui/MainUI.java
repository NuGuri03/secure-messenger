package client.ui;

import client.ChatClient;
import client.WindowManager;
import client.ui.panel.LobbyPanel;
import client.ui.panel.RecentChatPanel;
import client.ui.panel.SettingsPanel;
import client.ui.panel.SideBarPanel;

import javax.swing.*;
import java.awt.*;

public class MainUI extends BaseUI {
    private final JPanel lobbyPanel;
    private final JPanel recentChatPanel;
    private final JPanel settingsPanel;

    public MainUI(ChatClient client) {
        super(client);

        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension minSize = new Dimension(550, 700);
        setSize(minSize);
        setMinimumSize(minSize);
        setLayout(new BorderLayout());

        SideBarPanel sidebar = new SideBarPanel();
        sidebar.setPreferredSize(new Dimension(100, 0));

        JPanel mainPanel = new JPanel(new CardLayout());

        lobbyPanel = new LobbyPanel(client);
        recentChatPanel = new RecentChatPanel(client);
        settingsPanel = new SettingsPanel(client);

        mainPanel.add(lobbyPanel, "lobby");
        mainPanel.add(recentChatPanel, "chat");
        mainPanel.add(settingsPanel, "settings");

        WindowManager.initMainPanel(mainPanel);
        sidebar.lobbyButton.addActionListener(e -> WindowManager.showLobby());
        sidebar.chatButton.addActionListener(e -> WindowManager.showChat());
        sidebar.settingsButton.addActionListener(e -> WindowManager.showSettings());

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel getLobbyPanel() {
        return lobbyPanel;
    }
}
