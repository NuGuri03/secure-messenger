package client.ui;

import javax.swing.JFrame;
import client.ui.component.button.UserIconButton;

public class ProfileUI extends BaseUI{
    private static final long serialVersionUID = 1L;

    UserIconButton userIconButton;

    public ProfileUI() {
        userIconButton = new UserIconButton("/secure-messenger/src/main/resources/icons/default_profile.png", 32);

        // 클릭 시 ProfileUI 오픈
        userIconButton.addActionListener(e -> {
            ProfileUI profile = new ProfileUI();
            profile.setVisible(true);
        });

        add(userIconButton);

        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
