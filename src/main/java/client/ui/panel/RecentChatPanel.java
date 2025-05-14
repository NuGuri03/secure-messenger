package client.ui.panel;

import client.ui.ChatUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RecentChatPanel extends JPanel {
    public RecentChatPanel() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Recent");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton openChatButton = new JButton("채팅창 열기");
        openChatButton.addActionListener((ActionEvent e) -> {
            // ChatUI를 새 창으로 띄움
            SwingUtilities.invokeLater(() -> {
                ChatUI chatUI = new ChatUI("테스트유저");
                chatUI.setVisible(true);
            });
        });

        add(openChatButton, BorderLayout.CENTER);
    }
}
