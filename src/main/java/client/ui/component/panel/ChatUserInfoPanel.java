package client.ui.component.panel;

import client.ui.ChatUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ChatUserInfoPanel extends ClickAblePanel{
    private final String username;

    @Override
    protected void onClick(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            ChatUI chatUI = new ChatUI(username, "테스트유저");
            chatUI.setVisible(true);
        });
    }

    public ChatUserInfoPanel(String username) {
        this.username = username;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(null);

        
    }
}