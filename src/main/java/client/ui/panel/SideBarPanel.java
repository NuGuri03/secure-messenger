package client.ui.panel;

import client.ui.component.button.IconButton;

import javax.swing.*;
import java.awt.*;

public class SideBarPanel extends JPanel {
    public IconButton lobbyButton;
    public IconButton chatButton;
    public IconButton settingsButton;

    public SideBarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(80, 0));  // 너비 고정

        // 정렬 및 여백
        setAlignmentY(Component.TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(30, 5, 30, 5));

        // 배경
        setBackground(Color.decode("#A9A9A9"));

        lobbyButton = new IconButton("/icons/user.png", 28, "Lobby");
        chatButton = new IconButton("/icons/chat.png", 24, "Chat");
        settingsButton = new IconButton("/icons/settings.png", 24, "Settings");

        add(Box.createVerticalStrut(30)); // 간격
        add(lobbyButton);
        add(Box.createVerticalStrut(40)); // 간격
        add(chatButton);
        add(Box.createVerticalStrut(40)); // 간격
        add(settingsButton);
    }

}
