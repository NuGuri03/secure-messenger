package client.ui.component.panel;

import client.ui.ChatUI;
import client.ui.component.button.UserIconButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ChatInfoPanel extends ClickAblePanel{
    private final String myName;
    private final String username;
    private final String lastMessage;

    @Override
    protected void onClick(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            ChatUI chatUI = new ChatUI(myName, username);
            chatUI.setVisible(true);
        });
    }

    public ChatInfoPanel(String myName, String username, String lastMessage) {
        this.myName = myName;
        this.username = username;
        this.lastMessage = lastMessage;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);

        Font nameFont = new Font("Pretendard", Font.BOLD, 16);
        Font messageFont = new Font("Pretendard", Font.PLAIN, 14);

        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        UserIconButton userIconButton = new UserIconButton("/icons/default_profile.png", 40);
        userIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        avatarPanel.add(userIconButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(nameFont);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(userLabel);

        JLabel messageLabel = new JLabel(lastMessage);
        messageLabel.setFont(messageFont);
        messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        messageLabel.setForeground(Color.GRAY);
        contentPanel.add(messageLabel);

        add(Box.createRigidArea(new Dimension(15, 0)));
        add(avatarPanel);
        add(Box.createRigidArea(new Dimension(15, 0)));
        add(contentPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        setVisible(true);
    }
}