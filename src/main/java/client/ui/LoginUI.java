package client.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class LoginUI extends BaseUI {

    // 한글 입력을 영문 키보드 입력으로 치환하는 메서드
    private void forceEnglishTyping(JTextComponent textComponent) {
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // 한글 문자 범위
                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {

                    String korean = "ㅂㅈㄷㄱㅅㅛㅕㅑㅐㅔ" +
                                    "ㅁㄴㅇㄹㅎㅗㅓㅏㅣ" +
                                    "ㅋㅌㅊㅍㅠㅜㅡ";

                    String english = "qwertyuiop" +
                                     "asdfghjkl;" +
                                     "zxcvbnm,.";

                    int index = korean.indexOf(c);
                    if (index != -1 && index < english.length()) {
                        e.consume(); // 기존 문자 입력 막기
                        textComponent.replaceSelection(Character.toString(english.charAt(index)));
                    } else {
                        e.consume(); // 매핑되지 않은 한글 입력도 막기
                    }
                }
            }
        });
    }

    // 입력 글자 수 제한 클래스
    public class JTextLimit extends PlainDocument {
        private int limit;

        public JTextLimit(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            if (getLength() + str.length() <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    public LoginUI() {
        super();

        setTitle("환영합니다");
        setSize(450, 300);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font mainFont = new Font("Pretendard", Font.PLAIN, 16);
        Font subFont = new Font("Pretendard", Font.PLAIN, 14);

        JLabel labelID = new JLabel("아이디:");
        labelID.setFont(mainFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelID, gbc);

        JTextField textID = new JTextField(20);
        textID.setDocument(new JTextLimit(20));
        textID.setFont(mainFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(textID, gbc);

        JLabel labelPW = new JLabel("비밀번호:");
        labelPW.setFont(mainFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(labelPW, gbc);

        JPasswordField textPW = new JPasswordField(20);
        textPW.setDocument(new JTextLimit(25));
        textPW.setFont(mainFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(textPW, gbc);

        // 한글 → 영문 키보드 치환 적용
        forceEnglishTyping(textID);
        forceEnglishTyping(textPW);

        JButton btnLogin = new JButton("로그인");
        btnLogin.setFont(subFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnLogin, gbc);

        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");

        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        };

        textID.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "login");
        textID.getActionMap().put("login", loginAction);
        textPW.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "login");
        textPW.getActionMap().put("login", loginAction);

        JButton btnSignUp = new JButton("회원 가입");
        btnSignUp.setFont(subFont);
        gbc.gridy = 3;
        add(btnSignUp, gbc);

        String savedID = "user";
        String savedPW = "user";

        btnLogin.addActionListener(e -> {
            String inputID = textID.getText();
            String inputPW = new String(textPW.getPassword());

            if (inputID.isEmpty() || inputPW.isEmpty()) {
                return;
            }

            if (inputID.equals(savedID)) {
                if (inputPW.equals(savedPW)) {
                    JOptionPane.showMessageDialog(this, "로그인 성공!");
                    new MainUI(savedID);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                    textPW.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "존재 하지않는 아이디입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                textPW.setText("");
            }
        });

        btnSignUp.addActionListener(e -> {
            new SignUpUI();
        });

        setVisible(true);
    }
}
