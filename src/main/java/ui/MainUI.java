package ui;

import com.formdev.flatlaf.FlatLightLaf;
import ui.panel.LobbyPanel;
import ui.panel.RecentChatPanel;
import ui.panel.SettingsPanel;
import ui.panel.SideBarPanel;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    public MainUI(String username) {
        try {
            // LookAndFeel 플러그인 적용
            UIManager.setLookAndFeel(new FlatLightLaf());
            // 폰트를 Pretendard 로 설정
            Font customFont = new Font("Pretendard", Font.PLAIN, 14);
            UIManager.put("defaultFont", customFont);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        if (username == null || username.trim().isEmpty()) {
            username = "user";
        }

        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension minSize = new Dimension(550, 700);
        setSize(minSize);
        setMinimumSize(minSize);
        setLayout(new BorderLayout());

        SideBarPanel sidebar = new SideBarPanel();
        sidebar.setPreferredSize(new Dimension(100, 0));

        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.add(new LobbyPanel(username), "lobby");
        mainPanel.add(new RecentChatPanel(), "chat");
        mainPanel.add(new SettingsPanel(), "settings");

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }


}
