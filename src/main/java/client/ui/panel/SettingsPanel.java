package client.ui.panel;

import client.ChatClient;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private static final int PADDING = 30;

    public SettingsPanel(ChatClient client) {
        UserInfo myInfo = client.getUserInfo();

        this.setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("설정");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));

        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 0, 1, 0, Color.decode("#A9A9A9")));
        this.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        contentPanel.add(Box.createVerticalStrut(30));

        //이름 입력 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);

        JTextField nameField = new JTextField(myInfo.getUsername());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameField);

        contentPanel.add(Box.createVerticalStrut(20));

        //소개 입력 필드
        JLabel introLabel = new JLabel("소개 문구:");
        introLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(introLabel);

        JTextField introField = new JTextField(myInfo.getBio());
        introField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        introField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(introField);

        contentPanel.add(Box.createVerticalStrut(30));

        //저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.setBackground(Color.WHITE);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(saveButton);

        //저장 동작
        saveButton.addActionListener(e -> {
            String newUsername = nameField.getText().trim();
            String newBio = introField.getText().trim();

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력하세요.");
                return;
            } else if (!newUsername.matches("^.{1,32}$")) {
                JOptionPane.showMessageDialog(this, "이름을 1~32자로 입력하세요.");
                return;
            }

            client.setUsername(newUsername);
            client.setBio(newBio);

            JOptionPane.showMessageDialog(this, "수정되었습니다.");
        });

        this.add(contentPanel, BorderLayout.CENTER);
    }
}
