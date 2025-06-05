package client.ui;

import client.ChatClient;
import client.ui.component.text.JTextFieldLimit;
import networked.messages.RegisterResponse;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpUI extends BaseUI {
    public SignUpUI(ChatClient client) {
        super(client);

        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension minSize = new Dimension(430, 450);
        setMinimumSize(minSize);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        final Dimension TEXT_SIZE_DIMENSION = new Dimension(300, 30);
        Font font = new Font("Pretendard", Font.PLAIN, 14);
        this.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel label = new JLabel("이름", SwingConstants.RIGHT);
        gbc.anchor = GridBagConstraints.EAST;
        add(label, gbc);

        String namePlaceHolder = "이름을 1자 이상 32자 이하로 입력해주세요";
        JTextField nameField = new JTextField(namePlaceHolder);
        nameField.setDocument(new JTextFieldLimit(32));
        nameField.setPreferredSize(TEXT_SIZE_DIMENSION);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField.setFont(font);
        add(nameField, gbc);

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(namePlaceHolder)) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText(namePlaceHolder);
                    nameField.setForeground(Color.GRAY);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        add(new JLabel("ID", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        String idPlaceHolder = "소문자, 숫자, 특수기호(_ . -)만 사용할 수 있습니다";
        JTextField idField = new JTextField(idPlaceHolder);
        idField.setPreferredSize(TEXT_SIZE_DIMENSION);
        idField.setFont(font);
        add(idField, gbc);
        idField.setForeground(Color.GRAY);

        idField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (idField.getText().equals(idPlaceHolder)) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = idField.getText();
                if (text.isEmpty()) {
                    idField.setDocument(new PlainDocument());
                    idField.setText(idPlaceHolder);
                    idField.setForeground(Color.GRAY);
                }
            }
        });

        // 비밀번호
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        add(new JLabel("비밀번호", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField pwField = new JPasswordField();
        pwField.setPreferredSize(TEXT_SIZE_DIMENSION);
        pwField.setFont(font);
        add(pwField, gbc);

        gbc.insets = new Insets(0, 10, 3, 15);

        // 메시지 라벨
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel messageLabel = new JLabel("비밀번호는 8자 이상 1024자 이하로 입력해주세요", SwingConstants.CENTER);
        messageLabel.setFont(font);
        add(messageLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        // 비밀번호 확인
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("비밀번호 확인", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField pwcField = new JPasswordField();
        pwcField.setPreferredSize(TEXT_SIZE_DIMENSION);
        pwcField.setFont(font);
        add(pwcField, gbc);

        // 확인 버튼
        gbc.gridx=0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton confirm = new JButton("확인");
        confirm.setPreferredSize(new Dimension(100, 30));
        confirm.setBackground(Color.WHITE);
        confirm.setFont(font);
        add(confirm, gbc);

        confirm.addActionListener(e -> signUp(nameField, idField, pwField, pwcField));

        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirm.doClick();
            }
        };

        pwcField.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "confirm");
        pwcField.getActionMap().put("confirm", loginAction);

        setVisible(true);
        nameField.requestFocusInWindow();
    }

    private void signUp(JTextField nameField, JTextField idField, JPasswordField pwField, JPasswordField pwcField) {
        String name = nameField.getText();
        String id = idField.getText();
        String password = new String(pwField.getPassword());
        String confirmPassword = new String(pwcField.getPassword());

        // 한국어 정규식
        String koreanRegex = ".*[가-힣ㄱ-ㅎㅏ-ㅣ].*";

        if (id.matches(koreanRegex) || password.matches(koreanRegex)) {
            showCustomDialog("아이디와 비밀번호에는 한글을 포함할 수 없습니다");
            return;
        } else if (name.isEmpty() || id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showCustomDialog("모든 항목을 입력하세요");
            return;
        } else if (!password.equals(confirmPassword)) {
            showCustomDialog("비밀번호가 일치하지 않습니다");
            return;
        } else if (!name.matches("^.{1,32}$")) {
            showCustomDialog("이름은 1~32자로 작성하세요");
            return;
        } else if(!id.matches("^[a-z0-9_.\\-]{4,32}$")) {
            showCustomDialog("ID는 소문자,숫자,특수기호(_ . -)4~32자로 작성하세요");
            return;
        } else if(!password.matches("^[\\u0020-\\u007E]{8,1024}$")) {
            showCustomDialog("비밀번호는 8글자 이상, 1024글자 이하로 작성하세요");
            return;
        }

        requestSignUp(name, id, password);
    }

    private void requestSignUp(String name, String id, String password) {
        setFormEnabled(false);

        var client = getClient();
        client.setOneshotCallback(RegisterResponse.class, (RegisterResponse res) -> {
            if (res.success) {
                showCustomDialog("회원가입이 완료되었습니다");
                dispose();
            } else {
                showCustomDialog("회원가입에 실패했습니다: " + res.message);
                setFormEnabled(true);
            }
        });

        client.register(id, password, name);
    }

    private void showCustomDialog(String message) {
        JDialog dialog = new JDialog(this, "알림", true);
        dialog.setSize(350, 150);
        dialog.setFont(this.getFont());
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(this.getFont());
        label.setForeground(Color.BLACK);
        dialog.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("확인");
        okButton.setFont(this.getFont());
        okButton.setBackground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
        okButton.requestFocusInWindow();
    }
}
