package client.ui.component.panel;

import client.ChatClient;
import client.WindowManager;
import client.ui.component.button.UserIconButton;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;

public class UserInfoPanel extends ClickAblePanel {
    UserInfo userInfo;
    ChatClient client;

    @Override
    protected void onClick(MouseEvent e) {
        boolean roomExists = client.getPrivateRoomInfo(userInfo.getId()) != null;
        if (!roomExists) {
            // Create a new private room right away if it doesn't exist
            String roomName = "";
            client.createRoom(roomName, userInfo.getHandle());
        }

        var roomInfo = client.getPrivateRoomInfo(userInfo.getId());
        if (roomInfo == null) { return; }

        WindowManager.openChatUI(roomInfo);
    }

    public UserInfoPanel(ChatClient client, UserInfo userInfo) {
        this.userInfo = userInfo;
        this.client = client;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);

        // 이름 및 자기소개 패널
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel nameLabel = new JLabel(userInfo.getUsername());
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        JLabel bioLabel = new JLabel(userInfo.getBio());
        bioLabel.setFont(new Font("Pretendard", Font.PLAIN, 14));
        bioLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        bioLabel.setForeground(Color.GRAY);
        content.add(bioLabel);

        JPanel myAvatarPanel = new JPanel();
        myAvatarPanel.setLayout(new BoxLayout(myAvatarPanel, BoxLayout.Y_AXIS));
        myAvatarPanel.setOpaque(false);
        myAvatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.setBorder(new EmptyBorder(1, 0, 0, 0));

        UserIconButton userIconButton = new UserIconButton(userInfo, 40);
        userIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.add(userIconButton);

        add(Box.createRigidArea(new Dimension(35, 0)));
        add(myAvatarPanel);
        add(Box.createRigidArea(new Dimension(15, 0)));
        add(content);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
