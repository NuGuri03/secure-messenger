package client.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.*;


public class LoginUI extends BaseUI {
	public class JTextLimit extends PlainDocument {
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

	public LoginUI() {
        super();

		setTitle("환영합니다");
		setSize(450, 300);
		setResizable(true); // 창 크기 조절 가능
		setLocationRelativeTo(null);  // 창 화면 중앙 위치
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// GridBagLayout 사용 : 행과 열 배치 및 크기 지정
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();  // 세부 위치 설정
		gbc.insets = new Insets(10, 10, 10, 10); // 여백

		Font mainFont = new Font("Pretendard", Font.PLAIN, 16);
		Font subFont = new Font("Pretendard", Font.PLAIN, 14);

		// 아이디
		JLabel labelID = new JLabel("아이디:");
		labelID.setFont(mainFont);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelID, gbc);

		// 아이디 입력
		JTextField textID = new JTextField(20);
		textID.setDocument(new JTextLimit(20)); // 최대 15글자
		textID.setFont(mainFont);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		add(textID, gbc);

		// 비밀번호
		JLabel labelPW = new JLabel("비밀번호:");
		labelPW.setFont(mainFont);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		add(labelPW, gbc);

		//비밀번호 입력
		JPasswordField textPW = new JPasswordField(20);
		textPW.setDocument(new JTextLimit(25)); // 최대 20글자
		textPW.setFont(mainFont);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		add(textPW, gbc);

		// 로그인 버튼
		JButton btnLogin = new JButton("로그인");
		btnLogin.setFont(subFont);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnLogin, gbc);

		// Enter 키를 누를 시 로그인 버튼이 작동하게
		KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");

		// 공통 액션 객체
		Action loginAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick(); // 로그인 버튼 클릭과 동일한 동작 수행
			}
		};
		// 바인딩 적용
		textID.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "login");
		textID.getActionMap().put("login", loginAction);

		textPW.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "login");
		textPW.getActionMap().put("login", loginAction);


		// 회원가입 버튼
		JButton btnSignUp = new JButton("회원 가입");
		btnSignUp.setFont(subFont);
		gbc.gridy = 3;
		add(btnSignUp, gbc);
		
		// 로그인 실패 여부에 따른 다양한 버튼 이벤트
		String savedID = "user";  // 일단 예시 저장
		String savedPW = "user";
		
		// 로그인 시도
		btnLogin.addActionListener(e -> {
            String inputID = textID.getText();
            String inputPW = new String(textPW.getPassword());

			// 입력이 없을 시에 버튼 작동하지 않게
			if (inputID.isEmpty() || inputPW.isEmpty()) {
				return;
			}

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
		btnSignUp.addActionListener(e -> {
			new SignUpUI();
        });

		// test
		setVisible(true);
	}
}
