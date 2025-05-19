package client.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpUI extends BaseUI {
    // TODO 1. 회원가입 UI 만들기 (아이디, 비밀번호, 성함, 생년월일 입력받게 해야함)
    // TODO 2. 비밀번호에는 제약조건이 있어야함(제약조건을 어떻게 할지 의견을 내주셔야함)
    /**
     * 회원가입 UI 생성
     */
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwcField;
    private JLabel messageLabel;
    private boolean idExist;

    public SignUpUI() {
        super();
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 700);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font font = new Font("맑은 고딕", Font.PLAIN, 14);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("이름", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField();
        nameField.setFont(font);
        nameField.setPreferredSize(new Dimension(200, 30));
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("ID", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        idField = new JTextField();
        idField.setFont(font);
        idField.setPreferredSize(new Dimension(200, 30));
        panel.add(idField, gbc);

        // ID 확인 버튼
        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton idConfirm = new JButton("ID 확인");
        idConfirm.setBackground(Color.WHITE);
        idConfirm.setFont(font);
        panel.add(idConfirm, gbc);

        // 비밀번호
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("비밀번호", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pwField = new JPasswordField();
        pwField.setFont(font);
        pwField.setPreferredSize(new Dimension(200, 30));
        panel.add(pwField, gbc);

        // 비밀번호 확인
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("비밀번호 확인", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pwcField = new JPasswordField();
        pwcField.setFont(font);
        pwcField.setPreferredSize(new Dimension(200, 30));
        panel.add(pwcField, gbc);

        // 메시지 라벨
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        messageLabel = new JLabel("비밀번호는 대소문자, 특수문자 포함 6~20자로 작성하세요", SwingConstants.CENTER);
        messageLabel.setFont(font);
        panel.add(messageLabel, gbc);

        // 확인 버튼
        gbc.gridy++;
        gbc.gridwidth = 3;
        JButton confirm = new JButton("확인");
        confirm.setBackground(Color.WHITE);
        confirm.setFont(font);
        panel.add(confirm, gbc);

        add(panel);
        setVisible(true);

        idExist = false;

        idConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(idField.getText().isEmpty())
                {
                    showCustomDialog("아이디를 입력하세요");
                }
                else if (false)
                {
                    showCustomDialog("이미 존재하는 아이디입니다");
                    idExist = false;
                }
                else if(true)
                {
                    showCustomDialog("사용 가능한 아이디입니다");
                    idExist = true;
                }
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp();
            }
        });

        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new NoKoreanFilter());
        ((AbstractDocument) pwField.getDocument()).setDocumentFilter(new NoKoreanFilter());
        ((AbstractDocument) pwcField.getDocument()).setDocumentFilter(new NoKoreanFilter());
    }

    public void signUp()
    {
        String name = nameField.getText();
        String id = idField.getText();
        String password = new String(pwField.getPassword());
        String confirmPassword = new String(pwcField.getPassword());

        if (name.isEmpty() || id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            showCustomDialog("모든 항목을 입력하세요");
        }
        else if (!idExist)
        {
            showCustomDialog("아이디를 확인하세요");
        }
        else if (!password.equals(confirmPassword))
        {
            showCustomDialog("비밀번호가 일치하지 않습니다");
        }
        else if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\=\\-\\[\\]{}\"'/?.<>,]).{6,20}$"))
        {
            showCustomDialog("대소문자, 숫자, 특수기호 포함 6~20자로 입력하세요");
        }
        else
        {
            showCustomDialog("완료되었습니다");
        }
    }

    public class NoKoreanFilter extends DocumentFilter
    {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            if (!containsKorean(text) || isComposedText(attr)) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            if (!containsKorean(text) || isComposedText(attr)) {
                super.replace(fb, offset, length, text, attr);
            }
        }

        private boolean containsKorean(String text) {
            return text != null && text.matches(".*[가-힣ㄱ-ㅎㅏ-ㅣ].*");
        }

        // 조합 중 텍스트인지 확인 (한글 입력 도중 상태)
        private boolean isComposedText(AttributeSet attr) {
            return attr != null && attr.isDefined(StyleConstants.ComposedTextAttribute);
        }
    }

    private void showCustomDialog(String message) {
        JDialog dialog = new JDialog(this, "알림", true);
        dialog.setSize(300, 150);
        dialog.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("<html>" + message + "</html>", SwingConstants.CENTER);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        dialog.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("확인");
        okButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        okButton.setBackground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
