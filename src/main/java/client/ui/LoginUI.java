package client.ui;

import client.ChatClient;
import client.WindowManager;
import client.ui.component.text.JTextFieldLimit;
import networked.messages.LoginResponse;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginUI extends BaseUI {
    public LoginUI(ChatClient client) {
        super(client);

        setTitle("환영합니다");
        setSize(400, 300);
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

        JTextField textID = new JTextField(12);
        textID.setDocument(new JTextFieldLimit(20));
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

        JPasswordField textPW = new JPasswordField(12);
        textPW.setDocument(new JTextFieldLimit(20));
        textPW.setFont(mainFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(textPW, gbc);

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

        btnLogin.addActionListener(e -> {
            String inputID = textID.getText();
            String inputPW = new String(textPW.getPassword());

            if (inputID.isEmpty() || inputPW.isEmpty()) {
                return;
            }

            // 한국어 정규식
            String koreanRegex = ".*[가-힣ㄱ-ㅎㅏ-ㅣ].*";

            if (inputID.matches(koreanRegex) || inputPW.matches(koreanRegex)) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호에는 한글을 포함할 수 없습니다.");
                return;
            }

            loginRequest(inputID, inputPW);
        });

        btnSignUp.addActionListener(e -> WindowManager.openSignUpUI());

        setVisible(true);
    }

    private void loginRequest(String id, String password) {
        // 로그인 처리 로직
        var client = getClient();
        client.login(id, password);
        client.setOneshotCallback(LoginResponse.class, (LoginResponse response) -> {
            if (response.success) {
                WindowManager.toMainUI();
            } else {
                showErrorMessage("로그인 실패: " + response.message);
                setEnabled(true);
            }
        });

        // 처리 전까지 UI 비활성화
        setEnabled(false);
    }
}
