package ui.component.button;

import javax.swing.*;
import java.awt.*;

public class IconButton extends JButton {
    /**
     * 주어진 이미지 경로로 아이콘 버튼을 생성
     * @param path 이미지 경로 (예: "resources/icon/icon.png")
     * @param size 아이콘 크기 (예: 32 → 32x32)
     * @param toolTip 아이콘 툴팁 (예: icon)
     */
    public IconButton(String path, int size, String toolTip) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        setIcon(scaledIcon);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));

        setToolTipText(toolTip);
        setContentAreaFilled(false);

        setAlignmentX(Component.CENTER_ALIGNMENT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
