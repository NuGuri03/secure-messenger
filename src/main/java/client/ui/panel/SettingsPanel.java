package client.ui.panel;

import client.ui.component.panel.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JTextField nameField;
    private JTextField introField;
    private JButton saveButton;
    private UserInfoPanel myInfo;
    private static final int PADDING = 30;

    public SettingsPanel(UserInfoPanel myInfo) {
        this.myInfo = myInfo;

        this.setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        //title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("setting");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));

        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 0, 1, 0, Color.decode("#A9A9A9")));
        this.add(title, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        //내 정보 표시 패널 (UserInfoPanel)
        myInfo.setAlignmentX(Component.LEFT_ALIGNMENT);



        contentPanel.add(Box.createVerticalStrut(30));

        //이름 입력 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);

        nameField = new JTextField(myInfo.getUsername());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameField);

        contentPanel.add(Box.createVerticalStrut(20));

        //소개 입력 필드
        JLabel introLabel = new JLabel("소개 문구:");
        introLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(introLabel);

        introField = new JTextField(myInfo.getIntroduction());
        introField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        introField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(introField);

        contentPanel.add(Box.createVerticalStrut(30));

        //저장 버튼
        saveButton = new JButton("저장");
        saveButton.setBackground(Color.WHITE);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(saveButton);

        //저장 동작
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newIntro = introField.getText().trim();

            if (newName.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "이름을 입력하세요.");
                return;
            }
            else if (!newName.matches("^.{1,32}$"))
            {
                JOptionPane.showMessageDialog(this, "이름을 1~32자로 입력하세요.");
                return;
            }

            myInfo.setUsername(newName);
            myInfo.setIntroduction(newIntro);

            JOptionPane.showMessageDialog(this, "수정되었습니다.");
        });

        this.add(contentPanel, BorderLayout.CENTER);
    }
}
