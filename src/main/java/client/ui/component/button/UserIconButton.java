package client.ui.component.button;

import client.ResourceCache;
import client.WindowManager;
import networked.UserInfo;

import javax.swing.*;
import java.awt.*;

public class UserIconButton extends JButton {
    private final UserInfo userInfo;
    private final int avatarSize;

    public UserIconButton(UserInfo userInfo, int avatarSize) {
        this.userInfo = userInfo;
        this.avatarSize = avatarSize;

        setPreferredSize(new Dimension(avatarSize, avatarSize));

        int iconSize = (int)(avatarSize * 0.7);
        String imagePath = userInfo.getAvatarPath();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/images/default_profile.png";
        }

        ImageIcon icon = ResourceCache.getIcon(imagePath, iconSize);
        setIcon(icon);

        Dimension fixedSize = new Dimension(avatarSize, avatarSize);
        setPreferredSize(fixedSize);
        setMaximumSize(fixedSize);
        setMinimumSize(fixedSize);

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addActionListener(e -> {
            WindowManager.openProfileUI(userInfo);
        });
    }

    // 둥근 배경의 원 생성
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

    // 클릭 영역을 원형으로 제한
    @Override
    public boolean contains(int x, int y) {
        int r = avatarSize/2;
        int cx = avatarSize/2, cy = avatarSize/2;
        return (x-cx)*(x-cx) + (y-cy)*(y-cy) <= r*r;
    }

    public UserIconButton copy() {
        return new UserIconButton(userInfo, avatarSize);
    }
    
}