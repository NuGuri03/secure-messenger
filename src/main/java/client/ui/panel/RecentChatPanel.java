package client.ui.panel;

import client.ui.ChatUI;
import client.ui.component.panel.UserInfoPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RecentChatPanel extends JPanel {
    private UserInfoPanel myInfo;
    public RecentChatPanel(UserInfoPanel myInfo) {
        setLayout(new BorderLayout());

        this.myInfo = myInfo;

        JLabel label = new JLabel("Recent");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton openChatButton = new JButton("채팅창 열기");
        openChatButton.addActionListener((ActionEvent e) -> {
            // ChatUI를 새 창으로 띄움
            SwingUtilities.invokeLater(() -> {
                ChatUI chatUI = new ChatUI(myInfo.getUsername());
                chatUI.setVisible(true);
            });
        });

        add(openChatButton, BorderLayout.CENTER);
    }
}
