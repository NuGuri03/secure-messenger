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
//
//        UserIconButton userIconButton = new UserIconButton();

        JLabel userLabel = new JLabel(username);
        add(userLabel);

        JLabel messageLabel = new JLabel(lastMessage);
        messageLabel.setForeground(Color.GRAY);
        add(messageLabel);

        setAlignmentX(LEFT_ALIGNMENT);
        setAlignmentY(TOP_ALIGNMENT);
        setVisible(true);
    }
}