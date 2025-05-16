package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpUI extends JFrame {
    // TODO 1. 회원가입 UI 만들기 (아이디, 비밀번호, 성함, 생년월일 입력받게 해야함)
    // TODO 2. 비밀번호에는 제약조건이 있어야함(제약조건을 어떻게 할지 의견을 내주셔야함)
    /**
     * 회원가입 UI 생성
     */
    public SignUpUI() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        Container c = getContentPane();
        c.setLayout(null);

        //라벨, 입력창 위치 설정
        JLabel name = new JLabel("이름", SwingConstants.CENTER);
        name.setLocation(100, 20);
        name.setSize(100, 20);
        c.add(name);

        JTextField nameField  = new JTextField("");
        nameField.setLocation(200, 20);
        nameField.setSize(100, 20);
        c.add(nameField);

        JLabel id = new JLabel("id", SwingConstants.CENTER);
        id.setLocation(100, 40);
        id.setSize(100, 20);
        c.add(id);

        JTextField idField = new JTextField("");
        idField.setLocation(200, 40);
        idField.setSize(100, 20);
        c.add(idField);

        JLabel pw = new JLabel("비밀번호", SwingConstants.CENTER);
        pw.setLocation(100, 60);
        pw.setSize(100, 20);
        c.add(pw);

        JTextField pwField = new JTextField("");
        pwField.setLocation(200, 60);
        pwField.setSize(100, 20);
        c.add(pwField);

        JLabel wpc = new JLabel("비밀번호 확인", SwingConstants.CENTER);
        wpc.setLocation(100, 80);
        wpc.setSize(100, 20);
        c.add(wpc);

        JTextField pwcField = new JTextField("");
        pwcField.setLocation(200, 80);
        pwcField.setSize(100, 20);
        c.add(pwcField);

        JButton idConfirm = new JButton("id 확인");
        idConfirm.setLocation(300, 40);
        idConfirm.setSize(100, 20);
        c.add(idConfirm);

        JButton confirm = new JButton("확인");
        confirm.setLocation(200, 100);
        confirm.setSize(100, 20);
        c.add(confirm);

        setVisible(true);
    }
}
