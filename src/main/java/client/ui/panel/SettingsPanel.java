package client.ui.panel;

import javax.swing.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        JLabel label = new JLabel("Settings");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label);
    }
}
