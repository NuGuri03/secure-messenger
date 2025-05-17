package client.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpUI extends JFrame
{
    // TODO 1. 회원가입 UI 만들기 (아이디, 비밀번호, 성함, 생년월일 입력받게 해야함)
    // TODO 2. 비밀번호에는 제약조건이 있어야함(제약조건을 어떻게 할지 의견을 내주셔야함)
    /**
     * 회원가입 UI 생성
     */
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwcField;
    private boolean idExist;

    public SignUpUI() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        Container c = getContentPane();
        c.setLayout(null);

        //라벨, 입력창 위치 설정
        JLabel name = new JLabel("이름", SwingConstants.CENTER);
        name.setLocation(70, 20);
        name.setSize(100, 20);
        c.add(name);

        nameField  = new JTextField("");
        nameField.setLocation(170, 20);
        nameField.setSize(100, 20);
        c.add(nameField);

        JLabel id = new JLabel("id", SwingConstants.CENTER);
        id.setLocation(70, 40);
        id.setSize(100, 20);
        c.add(id);

        idField = new JTextField("");
        idField.setLocation(170, 40);
        idField.setSize(100, 20);
        c.add(idField);

        JLabel pw = new JLabel("비밀번호", SwingConstants.CENTER);
        pw.setLocation(70, 60);
        pw.setSize(100, 20);
        c.add(pw);

        pwField = new JPasswordField("");
        pwField.setLocation(170, 60);
        pwField.setSize(100, 20);
        c.add(pwField);

        JLabel pwc = new JLabel("비밀번호 확인", SwingConstants.CENTER);
        pwc.setLocation(70, 80);
        pwc.setSize(100, 20);
        c.add(pwc);

        pwcField = new JPasswordField("");
        pwcField.setLocation(170, 80);
        pwcField.setSize(100, 20);
        c.add(pwcField);

        JButton idConfirm = new JButton("id 확인");
        idConfirm.setLocation(270, 40);
        idConfirm.setSize(100, 20);
        c.add(idConfirm);

        JButton confirm = new JButton("확인");
        confirm.setLocation(170, 100);
        confirm.setSize(100, 20);
        c.add(confirm);

        JLabel messageLabel = new JLabel("비밀번호는 대소문자, 특수문자 포함6~20자로 작성하세요", SwingConstants.CENTER);
        messageLabel.setLocation(20, 125);
        messageLabel.setSize(400, 20);
        c.add(messageLabel);

        idExist = false;

        idConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(idField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(null, "아이디를 입력하세요");
                }
                else if (false)
                {
                    JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다");
                    idExist = false;
                }
                else if(true)
                {
                    JOptionPane.showMessageDialog(null, "사용 가능한 아이디입니다");
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

        setVisible(true);
    }

    public void signUp()
    {
        String name = nameField.getText();
        String id = idField.getText();
        String password = new String(pwField.getPassword());
        String confirmPassword = new String(pwcField.getPassword());

        if (name.isEmpty() || id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "모든 항목을 입력하세요");
        }
        else if (!idExist)
        {
            JOptionPane.showMessageDialog(null, "아이디를 확인하세요");
        }
        else if (!password.equals(confirmPassword))
        {
            JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다");
        }
        else if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\=\\-\\[\\]{}\"'/?.<>,]).{6,20}$"))
        {
            JOptionPane.showMessageDialog(null, "대소문자, 숫자, 특수기호 포함 6~20자로 입력하세요");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "완료되었습니다");
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
}



