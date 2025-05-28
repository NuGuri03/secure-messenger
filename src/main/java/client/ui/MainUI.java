package client.ui;

import client.ui.panel.LobbyPanel;
import client.ui.panel.RecentChatPanel;
import client.ui.panel.SettingsPanel;
import client.ui.panel.SideBarPanel;
import client.ui.component.panel.UserInfoPanel;

import javax.swing.*;
import java.awt.*;

public class MainUI extends BaseUI {

    public MainUI(String username) {
        super();

        if (username == null || username.trim().isEmpty()) {
            username = "user";
        }

        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension minSize = new Dimension(550, 700);
        setSize(minSize);
        setMinimumSize(minSize);
        setLayout(new BorderLayout());

        UserInfoPanel myInfo = new UserInfoPanel(username, "소개를 입력하세요", null);

        LobbyPanel lobbyPanel = new LobbyPanel(myInfo);
        SettingsPanel settingsPanel = new SettingsPanel(myInfo);
        RecentChatPanel chatPanel = new RecentChatPanel();

        SideBarPanel sidebar = new SideBarPanel();
        sidebar.setPreferredSize(new Dimension(100, 0));

        JPanel mainPanel = new JPanel(new CardLayout());

// mainPanel에 추가
        mainPanel.add(new LobbyPanel(myInfo), "lobby");
        mainPanel.add(chatPanel, "chat");
        mainPanel.add(new SettingsPanel(myInfo), "settings");

        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        sidebar.lobbyButton.addActionListener(e -> cardLayout.show(mainPanel, "lobby"));
        sidebar.chatButton.addActionListener(e -> cardLayout.show(mainPanel, "chat"));
        sidebar.settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "settings"));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }


}
