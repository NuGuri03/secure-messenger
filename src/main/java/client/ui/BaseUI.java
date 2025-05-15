package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * @brief BaseUI 클래스는 모든 창의 기본 클래스로, 공통적인 UI 설정을 적용한다.
 */
public abstract class BaseUI extends JFrame {
    private static final String MAIN_FONT_PATH = "/fonts/Pretendard.ttf";
    private static Font mainFont;

    public BaseUI() {
        ApplyLookAndFeel(new FlatLightLaf());
        ApplyMainFont();
    }

    private void ApplyMainFont() {
        if (mainFont != null) {
            UIManager.put("defaultFont", mainFont);
            return;
        }

        String fontPath = MAIN_FONT_PATH;
        try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                System.err.println("Font file not found: " + fontPath);
                return;
            }

            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);

            Font derivedFont = customFont.deriveFont(Font.PLAIN, 14f);
            mainFont = derivedFont;

            UIManager.put("defaultFont", mainFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApplyLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}