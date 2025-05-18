package client.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;


public class LoginUI extends BaseUI {
	public class JTextLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int limit;

	    public JTextLimit(int limit) {
	        this.limit = limit;
	    }

	    @Override
	    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
	        if (str == null) return;

	        if (getLength() + str.length() <= limit) {
	            super.insertString(offset, str, attr);
	        }
	    }
	}
	
	
	
	
// serialVersion UID
	private static final long serialVersionUID = 1L;
	
    // TODO 1. 로그인 UI 만들기
    /**
     * LoginUI 생
     */
	
	public LoginUI() {
		setTitle("로그인 하세요.");
		setSize(600, 400);
		setResizable(true); // 창 크기 조절 가능
		setLocationRelativeTo(null);  // 창 화면 중앙 위치
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// GridBagLayout 사용 : 행과 열 배치 및 크기 지정
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();  // 세부 위치 설정
		gbc.insets = new Insets(10, 10, 10, 10); // 여백

		
		// 아이디
		JLabel labelID = new JLabel("아이디:");
		labelID.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelID, gbc);

		// 아이디 입력
		JTextField textID = new JTextField(15);
		textID.setDocument(new JTextLimit(15)); // 최대 15글자
		textID.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(textID, gbc);

		// 비밀번호
		JLabel labelPW = new JLabel("비밀번호:");
		labelPW.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelPW, gbc);

		
		//비밀번호 입력
		JPasswordField textPW = new JPasswordField(15);
		textPW.setDocument(new JTextLimit(15)); // 최대 15글자
		textPW.setFont(new Font("굴림", Font.PLAIN, 18));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		add(textPW, gbc);

		
		// 로그인 버튼
		
		JButton btnLogin = new JButton("로그인");
		btnLogin.setFont(new Font("굴림", Font.BOLD, 16));
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnLogin, gbc);
		
		// 회원가입 버튼	
		JButton btnJoin = new JButton("회원 가입");
		btnJoin.setFont(new Font("굴림", Font.PLAIN, 16));
		gbc.gridy = 3;
		add(btnJoin, gbc);
		
		// 로그인 실패 여부에 따른 다양한 버튼 이벤트
		String savedID = "toturak3";  // 일단 예시 저장
		String savedPW = "1234hj";
		
		// 로그인 시도
		btnLogin.addActionListener(e -> {
            String inputID = textID.getText();
            String inputPW = new String(textPW.getPassword());

            if (inputID.equals(savedID)) {
            	if(inputPW.equals(savedPW))
            	{
            		JOptionPane.showMessageDialog(this, "로그인 성공!");

                    new MainUI(savedID);	
                    dispose(); // 현재 창 닫기
            	}else { // 비밀번호 불일치
                    JOptionPane.showMessageDialog(this,"비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                    textPW.setText(""); 
            	}
            } else { // 존재하지않는 아이디
                JOptionPane.showMessageDialog(this, "존재 하지않는 아이디입니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                textPW.setText(""); 
            }
        });
		
		// 회원 가입 시도
		btnJoin.addActionListener(e -> {
			new SignUpUI();
        });
		
		
		setVisible(true);
	}
}
