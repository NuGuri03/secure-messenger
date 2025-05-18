package client.ui;

import java.awt.*;
import javax.swing.*;


public class LoginUI extends JFrame {
	// serialVersion UID
	private static final long serialVersionUID = 1L;
	
    // TODO 1. 로그인 UI 만들기
    /**
     * LoginUI 생성
     */
	
	public LoginUI() {
		setTitle("로그인 하세요.");
		setSize(600, 400);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// GridBagLayout 사용
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // 여백

		JLabel labelID = new JLabel("아이디:");
		labelID.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelID, gbc);

		JTextField textID = new JTextField(15);
		textID.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		add(textID, gbc);

		JLabel labelPW = new JLabel("비밀번호:");
		labelPW.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelPW, gbc);

		JPasswordField textPW = new JPasswordField(15);
		textPW.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		add(textPW, gbc);

		JButton btnLogin = new JButton("로그인");
		btnLogin.setFont(new Font("굴림", Font.BOLD, 16));
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnLogin, gbc);

		JButton btnJoin = new JButton("회원 가입");
		btnJoin.setFont(new Font("굴림", Font.PLAIN, 16));
		gbc.gridy = 3;
		add(btnJoin, gbc);

		setVisible(true);
	}
    
}
