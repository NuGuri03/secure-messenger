package client.ui.panel;

import client.ui.component.button.UserIconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LobbyPanel extends JPanel {
    public LobbyPanel(String username) {
        setLayout(new BorderLayout());
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel myInfo = createMyInfoPanel(username);
        add(myInfo);
    }

    private JPanel createMyInfoPanel(String username) {
        JPanel myInfo = new JPanel();
        myInfo.setLayout(new BoxLayout(myInfo, BoxLayout.X_AXIS));
        myInfo.setOpaque(false);

        // 이름, 자기소개 패널
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        // 이름과 자기소개 사이 간격
        content.add(Box.createRigidArea(new Dimension(0, 2)));

        JLabel introduceLabel = new JLabel("Introduce");
        introduceLabel.setFont(new Font("Pretendard", Font.PLAIN, 14));
        introduceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        introduceLabel.setForeground(Color.GRAY);
        content.add(introduceLabel);

        JPanel myAvatarPanel = new JPanel();
        myAvatarPanel.setLayout(new BoxLayout(myAvatarPanel, BoxLayout.Y_AXIS));
        myAvatarPanel.setOpaque(false);
        myAvatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.setBorder(new EmptyBorder(1, 0, 0, 0));

        UserIconButton userIconButton = new UserIconButton("/icon/default_profile.png", 40);
        userIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.add(userIconButton);

        myInfo.add(Box.createRigidArea(new Dimension(0, 5)));
        myInfo.add(Box.createRigidArea(new Dimension(15, 0)));
        myInfo.add(myAvatarPanel);
        myInfo.add(Box.createRigidArea(new Dimension(10, 0)));
        myInfo.add(content);

        return myInfo;
    }
}
