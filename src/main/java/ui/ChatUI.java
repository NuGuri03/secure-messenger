package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatUI extends JFrame {
    private String username;

    public ChatUI(String username) {
        this.username = username;

        if (username == null || username.trim().isEmpty()) {
            username = "user";
        }

        setTitle("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(525, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 사이드바 패널
        JPanel sidebar = getSidebarPanel();

        // 채팅창 패널
        JPanel chatPanel = getChatPanel();

        // 탑바 영역
        JPanel topbar = getTopbarPanel(username);

        // 채팅 내용 출력 영역
        JTextArea chatArea = getChatArea();
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // 메세지 입력창 영역
        JPanel inputPanel = getInputPanel(chatArea, username);

        // 위치 설정
        chatPanel.add(topbar, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private JPanel getSidebarPanel() {
        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(100, 600));
        sidebar.setBackground(Color.LIGHT_GRAY);
        sidebar.setLayout(new GridLayout(3, 1));

        // 나중에 이미지로 교체 필요
        sidebar.add(new JButton("👤"));
        sidebar.add(new JButton("💬"));
        sidebar.add(new JButton("⚙️"));

        return sidebar;
    }

    private JPanel getTopbarPanel(String username) {
        JPanel topbar = new JPanel();

        JLabel usernameLabel = new JLabel(String.format("Chatting with %s", username));
        topbar.add(usernameLabel);

        return topbar;
    }

    private JPanel getChatPanel() {
        JPanel chat = new JPanel();
        chat.setLayout(new BorderLayout());

        return chat;
    }

    private JTextArea getChatArea() {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        chatArea.setBackground(Color.LIGHT_GRAY);
        chatArea.setLineWrap(true);

        return chatArea;
    }

    private JPanel getInputPanel(JTextArea chatArea, String username) {

        // 메세지 입력창
        JTextArea inputArea = new JTextArea();
        inputArea.setEditable(true);
        inputArea.setLineWrap(true);
        inputArea.setPreferredSize(new Dimension(0, 40));

        // 메세지 입력창에 "메세지 입력" 문구 띄우기
        String placeholder = "메세지 입력";
        inputArea.setText(placeholder);
        inputArea.setForeground(Color.GRAY);

        /* 메세지 입력창이 클릭되었을 때
        * "메세지 입력" 문구 지우고
        * 입력창 글자 색깔 검은색으로 바꾸기 */
        inputArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputArea.getText().equals(placeholder)) {
                    inputArea.setText("");
                    inputArea.setForeground(Color.BLACK);
                }
            }
        });

        // 전송 버튼
        JButton sendButton = new JButton("전송");
        sendButton.setPreferredSize(new Dimension(80, 40));

        // Enter 누를 시 "전송" 버튼 동작
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendButton.doClick();
                }
            }
        });

        // 전송 버튼 클릭 시 입력된 메세지를 채팅창에 추가
        sendButton.addActionListener(e -> {
            String message = inputArea.getText();

            if (message != null && !message.trim().isEmpty()) {
                chatArea.append(username + ": " + message.trim() + "\n");
                inputArea.setText("");
            }
        });

        JPanel input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));

        input.add(inputArea);
        input.add(sendButton);

        return input;
    }
}
