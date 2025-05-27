package client.ui.component.panel;

import client.ChatClient;
import client.ui.component.button.UserIconButton;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserInfoPanel extends JPanel {
    UserInfo userInfo;

    public UserInfoPanel(UserInfo userInfo) {
        this.userInfo = userInfo;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);

        // 이름 자기소개 패널
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel nameLabel = new JLabel(userInfo.getNickname());
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        JLabel introduceLabel = new JLabel(userInfo.getBio());
        introduceLabel.setFont(new Font("Pretendard", Font.PLAIN, 14));
        introduceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        introduceLabel.setForeground(Color.GRAY);
        content.add(introduceLabel);

        JPanel myAvatarPanel = new JPanel();
        myAvatarPanel.setLayout(new BoxLayout(myAvatarPanel, BoxLayout.Y_AXIS));
        myAvatarPanel.setOpaque(false);
        myAvatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.setBorder(new EmptyBorder(1, 0, 0, 0));

        UserIconButton userIconButton = new UserIconButton("/images/default_profile.png", 40);
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
