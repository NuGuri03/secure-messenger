package ui.component;

import javax.swing.*;
import java.awt.*;

public class UserIconButton extends JButton {

    public UserIconButton(String imagePath, int diameter) {
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(diameter - 10, diameter - 10, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        this.setIcon(scaledIcon);
        this.setContentAreaFilled(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setPreferredSize(new Dimension(diameter, diameter));
    }

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

    @Override
    public boolean contains(int x, int y) {
        int size = Math.min(getWidth(), getHeight());
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = size / 2;
        return (Math.pow(x - cx, 2) + Math.pow(y - cy, 2)) <= radius * radius;
    }
}
