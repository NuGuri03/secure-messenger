package client.ui;

import java.awt.Font;
import javax.swing.*;


public class LoginUI extends JFrame {
	// serialVersion UID
	private static final long serialVersionUID = 1L;
	
    // TODO 1. 로그인 UI 만들기
    // TODO 2. 아이디 찾기, 비밀번호 찾기 UI 만들기
    /**
     * LoginUI 생성
     */
    public LoginUI() {
    	setTitle("로그인 하세요.");
    	setSize(1000,500);
    	setLayout(null);
    	setResizable(false);
    	setLocationRelativeTo(null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
    	
    	
    	JButton btn1 = new JButton("로그인");
    	JButton btn2 = new JButton("아이디 찾기");
    	JButton btn3 = new JButton("비밀번호 찾기");
    	
    	btn1.setBounds(700,150,130,130);
    	btn2.setBounds(300,400,150,40);
    	btn3.setBounds(500,400,150,40);
    	
    	add(btn1);
    	add(btn2);
    	add(btn3);
    	
    	setVisible(true);
    }
    
    public static void main(String[] args)
    {
    	UIManager.put("Button.font", new Font("굴림", Font.PLAIN, 14)); // 폰트 설정
        SwingUtilities.invokeLater(() -> new LoginUI()); // LoginUI 생성
    }
}
