package client;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import client.ui.ChatUI;
import client.ui.LoginUI;
import client.ui.MainUI;

public class ClientMain {
    public static void main(String[] args) {
       
        // LoginUI  -> mainUI  / SignUpUI
        SwingUtilities.invokeLater(() -> new LoginUI());
    }
}