package client.ui.panel;

import client.ui.component.button.UserIconButton;

import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {
    public LobbyPanel(String username) {

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
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 10));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        // 이름과 자기소개 사이 간격
        content.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel introduceLabel = new JLabel("Introduce");
        introduceLabel.setFont(new Font("Pretendard", Font.PLAIN, 8));
        introduceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(introduceLabel);

        UserIconButton userIconButton = new UserIconButton("/icon/default_profile.png", 32);
        userIconButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        myInfo.add(userIconButton);
        myInfo.add(Box.createRigidArea(new Dimension(4, 0)));
        myInfo.add(content);

        return myInfo;
    }
}
