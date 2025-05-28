package client.ui.panel;

import client.ui.component.panel.UserInfoPanel;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JTextField nameField;
    private JTextField introField;
    private JButton saveButton;
    private UserInfoPanel myInfo;

    public SettingsPanel(UserInfoPanel myInfo) {
        this.myInfo = myInfo;

        JLabel label = new JLabel("Settings");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label);

        this.myInfo = myInfo;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(Color.WHITE);

        //내 정보 표시 패널 (UserInfoPanel)
        myInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(myInfo);

        add(Box.createVerticalStrut(30));

        //이름 입력 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(nameLabel);

        nameField = new JTextField(myInfo.getUsername());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(nameField);

        add(Box.createVerticalStrut(20));

        //소개 입력 필드
        JLabel introLabel = new JLabel("소개 문구:");
        introLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(introLabel);

        introField = new JTextField(myInfo.getIntroduction());
        introField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        introField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(introField);

        add(Box.createVerticalStrut(30));

        //저장 버튼
        saveButton = new JButton("저장");
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(saveButton);

        //저장 동작
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newIntro = introField.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력하세요.");
                return;
            }

            myInfo.setUsername(newName);
            myInfo.setIntroduction(newIntro);

            JOptionPane.showMessageDialog(this, "내 정보가 수정되었습니다.");
        });
    }
}
