package client.ui;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import com.formdev.flatlaf.FlatLightLaf;

import client.ChatClient;

/**
 * @brief BaseUI 클래스는 모든 창의 기본 클래스로, 공통적인 UI 설정을 적용한다.
 */
public abstract class BaseUI extends JFrame {
    private static final String MAIN_FONT_PATH = "/fonts/Pretendard.ttf";
    private static Font mainFont;

    private ChatClient client;

    public BaseUI(ChatClient client) {
        this.client = client;
        applyLookAndFeel(new FlatLightLaf());
        applyMainFont();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    public void setFormEnabled(boolean enabled) {
        for (Component component : getContentPane().getComponents()) {
            var isInteractive = component instanceof JButton
                || component instanceof JTextField
                || component instanceof JCheckBox
                || component instanceof JComboBox;

            if (isInteractive) {
                component.setEnabled(enabled);
            }
        }
    }


    protected ChatClient getClient() {
        return client;
    }


    private void applyMainFont() {
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

    private void applyLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}