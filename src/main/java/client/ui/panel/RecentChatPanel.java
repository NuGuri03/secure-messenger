package client.ui.panel;

import client.ui.ChatUI;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RecentChatPanel extends JPanel {
    private static final int PADDING = 30;

    public RecentChatPanel(String username) {
        setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("나의 최근 채팅");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));

        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));
        add(title, BorderLayout.NORTH);

        JButton openChatButton = new JButton("채팅창 열기");
        openChatButton.addActionListener((ActionEvent e) -> {
            // ChatUI를 새 창으로 띄움
            SwingUtilities.invokeLater(() -> {
                ChatUI chatUI = new ChatUI(username,"테스트유저");
                chatUI.setVisible(true);
            });
        });

        add(openChatButton, BorderLayout.CENTER);
    }
}
