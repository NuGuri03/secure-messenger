package client.ui.panel;

import client.ChatClient;
import client.ui.component.panel.ChatInfoPanel;
import networked.RoomInfo;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;

public class RecentChatPanel extends JPanel {
    private static final int PADDING = 30;

    public RecentChatPanel() {
        setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("나의 최근 채팅");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));
        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));
        add(title, BorderLayout.NORTH);


        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chatListPanel.setBorder(BorderFactory.createEmptyBorder(PADDING / 2, PADDING / 2, PADDING / 2, PADDING / 2));

        ArrayList<ChatInfoPanel> recentChatList = new ArrayList<>();

        // Sample data for recent chats
        for (int i = 1; i <= 10; i++) {
            int roomId = 1000 + i;
            String roomName = "Test" + i;
            String handle = "user" + i;
            String nickname = "Test User" + i;
            recentChatList.add(new ChatInfoPanel(
                    new RoomInfo(roomId, roomName, null, null),
                    new UserInfo(handle, nickname, null, null, null)
            ));
        }

        for (var chatInfoPanel : recentChatList) {
            chatListPanel.add(chatInfoPanel);
            chatListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between panels
        }

        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#A9A9A9")));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }
}
