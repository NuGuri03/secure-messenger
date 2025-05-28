package client.ui.component.panel;

import client.ui.component.button.UserIconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserInfoPanel extends JPanel {
    private String username;
    private String introduction;
    private String avatarPath;

    private JLabel nameLabel;
    private JLabel introduceLabel;

    public UserInfoPanel(String username, String introduction, String imagePath) {
        if (username == null || username.trim().isEmpty()) {
            this.username = "user";
        } else {
            this.username = username;
        }

        if (introduction == null || introduction.trim().isEmpty()) {
            this.introduction = "introduction";
        } else {
            this.introduction = introduction;
        }

        if (imagePath == null || imagePath.trim().isEmpty()) {
            this.avatarPath = "/icons/default_profile.png";
        } else {
            this.avatarPath = imagePath;
        }

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);

        // 이름 자기소개 패널
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel nameLabel = new JLabel(this.username);
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        JLabel introduceLabel = new JLabel(this.introduction);
        introduceLabel.setFont(new Font("Pretendard", Font.PLAIN, 14));
        introduceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        introduceLabel.setForeground(Color.GRAY);
        content.add(introduceLabel);

        JPanel myAvatarPanel = new JPanel();
        myAvatarPanel.setLayout(new BoxLayout(myAvatarPanel, BoxLayout.Y_AXIS));
        myAvatarPanel.setOpaque(false);
        myAvatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.setBorder(new EmptyBorder(1, 0, 0, 0));

        UserIconButton userIconButton = new UserIconButton(this.avatarPath, 40);
        userIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        myAvatarPanel.add(userIconButton);

        add(Box.createRigidArea(new Dimension(35, 0)));
        add(myAvatarPanel);
        add(Box.createRigidArea(new Dimension(15, 0)));
        add(content);
    }

    public String getUsername()
    {
        return username;
    }

    public String getIntroduction()
    {
        return introduction;
    }

    public void setUsername(String name)
    {
        this.username = name;
        if (nameLabel != null) {
            nameLabel.setText(name);
        }
    }

    public void setIntroduction(String intro)
    {
        this.introduction = intro;
        if (introduceLabel != null) {
            introduceLabel.setText(intro);
        }
    }

}
