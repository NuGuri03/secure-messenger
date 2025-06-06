package client.ui.component.panel;

import client.WindowManager;
import client.ui.component.button.UserIconButton;
import networked.RoomInfo;
import networked.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ChatInfoPanel extends ClickAblePanel{
    private RoomInfo roomInfo;

    @Override
    protected void onClick(MouseEvent e) {
        WindowManager.openChatUI(roomInfo);
    }

    public ChatInfoPanel(RoomInfo roomInfo, String name, UserInfo friendInfo) {
        this.roomInfo = roomInfo;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        setPreferredSize(new Dimension(350, 55));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        Font nameFont = new Font("Pretendard", Font.BOLD, 16);
        Font messageFont = new Font("Pretendard", Font.PLAIN, 14);

        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        UserIconButton userIconButton = new UserIconButton(friendInfo, 40);
        userIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        avatarPanel.add(userIconButton);

        // 채팅방 정보
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // 채팅방 이름
        JLabel chatLabel = new JLabel(name);
        chatLabel.setFont(nameFont);
        chatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(chatLabel);

        String lastMessage = roomInfo.getLastMessage();
        JLabel messageLabel = new JLabel(lastMessage);
        messageLabel.setFont(messageFont);
        messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        messageLabel.setForeground(Color.GRAY);
        contentPanel.add(messageLabel);

        add(Box.createRigidArea(new Dimension(15, 0)));
        add(avatarPanel);
        add(Box.createRigidArea(new Dimension(15, 0)));
        add(contentPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        setVisible(true);
    }
}