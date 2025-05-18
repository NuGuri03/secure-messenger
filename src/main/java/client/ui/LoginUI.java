package client.ui;

import java.awt.*;
import javax.swing.*;

public class LoginUI extends BaseUI {
    private static final long serialVersionUID = 1L;
    /**
     * LoginUI 생성
     */
	public LoginUI() {
        super();

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
		labelID.setFont(new Font("pretendard", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelID, gbc);

		JTextField textID = new JTextField(15);
		textID.setFont(new Font("pretendard", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		add(textID, gbc);

		JLabel labelPW = new JLabel("비밀번호:");
		labelPW.setFont(new Font("pretendard", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelPW, gbc);

		JPasswordField textPW = new JPasswordField(15);
		textPW.setFont(new Font("pretendard", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		add(textPW, gbc);

		JButton btnLogin = new JButton("로그인");
		btnLogin.setFont(new Font("pretendard", Font.BOLD, 16));
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnLogin, gbc);

		JButton btnJoin = new JButton("회원 가입");
		btnJoin.setFont(new Font("pretendard", Font.PLAIN, 16));
		gbc.gridy = 3;
		add(btnJoin, gbc);

		// 로그인 실패 여부에 따른 다양한 버튼 이벤트

		String savedID = "user";  // 일단 예시 저장
		String savedPW = "user";

		btnLogin.addActionListener(e -> {
            String inputID = textID.getText();
            String inputPW = new String(textPW.getPassword());

            if (inputID.equals(savedID) && inputPW.equals(savedPW)) {
                JOptionPane.showMessageDialog(this, "로그인 성공!");

                // 다음 창 띄우기
                JFrame nextFrame = new JFrame("메인 화면");
                nextFrame.setSize(300, 200);
                nextFrame.setLocationRelativeTo(null);
                nextFrame.add(new JLabel("환영합니다, " + inputID + "!", SwingConstants.CENTER));
                nextFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                nextFrame.setVisible(true);

                dispose(); // 현재 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);

                new LoginUI(); // 다시 로그인 창 열기
                dispose(); // 현재 창 닫기
            }
        });
		setVisible(true);
	}
}
