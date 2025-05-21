package client.ui.panel;

import client.WindowManager;
import networked.RoomInfo;

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
            // TODO
            WindowManager.openChatUI(new RoomInfo(1234, "Test", null, null));
        });

        add(openChatButton, BorderLayout.CENTER);
    }
}
