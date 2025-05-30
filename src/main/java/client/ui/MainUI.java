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

        mainPanel.add(new LobbyPanel(client), "lobby");
        mainPanel.add(new RecentChatPanel(), "chat");
        mainPanel.add(new SettingsPanel(client), "settings");

        WindowManager.initMainPanel(mainPanel);

        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        sidebar.lobbyButton.addActionListener(e -> WindowManager.showLobby());
        sidebar.chatButton.addActionListener(e -> WindowManager.showChat());
        sidebar.settingsButton.addActionListener(e -> WindowManager.showSettings());

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }


}
