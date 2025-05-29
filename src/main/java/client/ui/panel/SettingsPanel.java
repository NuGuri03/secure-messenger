package client.ui.panel;

import client.ChatClient;

import javax.swing.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(ChatClient client) {
        JLabel label = new JLabel("Settings");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label);

        // TODO: 계정 정보 변경(닉네임, 이메일 등)
    }
}
