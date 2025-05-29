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

    public RecentChatPanel(ChatClient client) {
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
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1001, "Test1", null, null),
                new UserInfo("user1", "Test User1", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1002, "Test2", null, null),
                new UserInfo("user2", "Test User2", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1003, "Test3", null, null),
                new UserInfo("user3", "Test User3", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1004, "Test4", null, null),
                new UserInfo("user4", "Test User4", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1005, "Test5", null, null),
                new UserInfo("user5", "Test User5", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1006, "Test6", null, null),
                new UserInfo("user6", "Test User6", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1007, "Test7", null, null),
                new UserInfo("user7", "Test User7", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1008, "Test8", null, null),
                new UserInfo("user8", "Test User8", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1009, "Test9", null, null),
                new UserInfo("user9", "Test User9", null, null, null)
        ));
        recentChatList.add(new ChatInfoPanel(
                new RoomInfo(1010, "Test10", null, null),
                new UserInfo("user10", "Test User10", null, null, null)
        ));

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
