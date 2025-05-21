package client.ui;

import client.ui.component.text.KoreanFilter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SignUpUI extends BaseUI {
    public SignUpUI() {
        super();

        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 700);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font font = new Font("Pretendard", Font.PLAIN, 14);
        this.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("이름", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField nameField = new JTextField("1~32자로 입력하세요");
        nameField.setFont(font);
        nameField.setPreferredSize(new Dimension(200, 30));
        panel.add(nameField, gbc);
        nameField.setForeground(Color.GRAY);

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("1~32자로 입력하세요")) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("1~32자로 입력하세요");
                    nameField.setForeground(Color.GRAY);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("ID", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField idField = new JTextField("소문자,숫자,특수기호(_ . -)4~32자로 작성하세요");
        idField.setFont(font);
        idField.setPreferredSize(new Dimension(200, 30));
        panel.add(idField, gbc);
        idField.setForeground(Color.GRAY);

        idField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (idField.getText().equals("소문자,숫자,특수기호(_ . -)4~32자로 작성하세요")) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = idField.getText();
                if (text.isEmpty() || text.equals("소문자,숫자,특수기호(_ . -)4~32자로 작성하세요")) {
                    // placeholder 복구
                    idField.setDocument(new PlainDocument());
                    idField.setText("소문자,숫자,특수기호(_ . -)4~32자로 작성하세요");
                    idField.setForeground(Color.GRAY);
                    ((AbstractDocument) idField.getDocument()).setDocumentFilter(new KoreanFilter());
                }
            }
        });

        // 비밀번호
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("비밀번호", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField pwField = new JPasswordField();
        pwField.setFont(font);
        pwField.setPreferredSize(new Dimension(200, 30));
        panel.add(pwField, gbc);

        // 메시지 라벨
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel messageLabel = new JLabel("비밀번호는 8글자 이상, 1024글자 이하로 작성하세요", SwingConstants.CENTER);
        messageLabel.setFont(font);
        panel.add(messageLabel, gbc);

        // 비밀번호 확인
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("비밀번호 확인", SwingConstants.CENTER), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField pwcField = new JPasswordField();
        pwcField.setFont(font);
        pwcField.setPreferredSize(new Dimension(420, 30));
        panel.add(pwcField, gbc);

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
        panel.add(confirm, gbc);

        add(panel);
        setVisible(true);

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp(nameField, idField, pwField, pwcField);
            }
        });

        // .setDocumentFilter 메서드는 AbstractDocument 클래스로 정의 되어 있음으로 다운캐스팅을 해야한다
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new KoreanFilter());
        ((AbstractDocument) pwField.getDocument()).setDocumentFilter(new KoreanFilter());
        ((AbstractDocument) pwcField.getDocument()).setDocumentFilter(new KoreanFilter());
    }

    private void signUp(JTextField nameField, JTextField idField, JPasswordField pwField, JPasswordField pwcField) {
        String name = nameField.getText();
        String id = idField.getText();
        String password = new String(pwField.getPassword());
        String confirmPassword = new String(pwcField.getPassword());

        ArrayList<String> idList = new ArrayList<>();
        idList.add("user");

        if (name.isEmpty() || id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showCustomDialog("모든 항목을 입력하세요");
        } else if (!password.equals(confirmPassword)) {
            showCustomDialog("비밀번호가 일치하지 않습니다");
        } else if (!name.matches("^.{1,32}$")) {
            showCustomDialog("이름은 1~32자로 작성하세요");
        } else if (idList.contains(id)) {
            showCustomDialog("중복된 아이디입니다");
        } else if(!id.matches("^[a-z0-9_.\\-]{4,32}$")) {
            showCustomDialog("ID는 소문자,숫자,특수기호(_ . -)4~32자로 작성하세요");
        } else if(!password.matches("^[\\u0020-\\u007E]{8,1024}$")) {
            showCustomDialog("비밀번호는 8글자 이상, 1024글자 이하로 작성하세요");
        } else {
            showCustomDialog("완료되었습니다");
        }
    }

    private void showCustomDialog(String message) {
        JDialog dialog = new JDialog(this, "알림", true);
        dialog.setSize(300, 150);
        dialog.setFont(this.getFont());
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("<html>" + message + "</html>", SwingConstants.CENTER);
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
    }
}
