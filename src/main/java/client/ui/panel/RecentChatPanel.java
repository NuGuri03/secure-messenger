package client.ui.panel;

import client.ChatClient;
import client.ui.component.panel.ChatInfoPanel;
import networked.RoomInfo;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        //iterate throught the hash map of client HashMap<RoomInfo, List<UserInfo>> rooms = new Ha`shMap<>();
        for (Map.Entry<RoomInfo, List<UserInfo>> entry : client.rooms.entrySet()) {

            RoomInfo room = entry.getKey();
            List<UserInfo> users = entry.getValue();

            if (room.getName().isEmpty())
            {
                for (UserInfo user : users) {
                    if (!user.getHandle().equals(client.getUserInfo().getHandle())) {
                        room.setName(user.getUsername());
                    }
                }
            }

            if (!users.isEmpty()) {

                for(UserInfo user : users) {
                    if (!user.getHandle().equals(client.getUserInfo().getHandle())) {
                        recentChatList.add(new ChatInfoPanel(room, user));
                    }
                }

            }
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
