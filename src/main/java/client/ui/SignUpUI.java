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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
