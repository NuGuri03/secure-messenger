package ui.component.panel;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(new Color(220, 220, 220)); // 연한 회색 배경
        g2.fillOval(x, y, size, size);

        g2.dispose();
        super.paintComponent(g);
    }
}
