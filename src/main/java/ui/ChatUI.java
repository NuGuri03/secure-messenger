package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

public class ChatUI extends JFrame {

    public ChatUI() {

        setTitle("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(450, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 사이드바
        JPanel sidebar = getSidebarPanel();

        // 채팅창
        JPanel chatPanel = getChatPanel();

        // topbar(어떤 유저와 채팅하는 지 표시)
        JPanel topbar = getTopbarPanel(null);

        // 채팅창
        JTextArea chatArea = getChatArea();
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // 메세지 입력창
        JPanel inputPanel = getInputPanel();

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

        if (username == null || username.trim().isEmpty()) {
            username = "unknown_user";
        }

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

        return chatArea;
    }

    private JPanel getInputPanel() {

        JTextField inputField = new JTextField(15);
        inputField.setBounds(10, 10, 600, 30);

        // "메세지 입력" 문구 띄우기
        String placeholder = "메세지 입력";
        inputField.setText(placeholder);
        inputField.setForeground(Color.GRAY);

        /* 메세지 입력창이 클릭되었을 때
        * "메세지 입력" 문구 지우고
        * 입력창 글자 색깔 검은색으로 바꾸기 */
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(placeholder)) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }
        });

        JButton sendButton = new JButton("전송");
        sendButton.setBounds(620, 10, 80, 30);

        JPanel input = new JPanel(null);
        input.setPreferredSize(new Dimension(800, 50));

        input.add(inputField);
        input.add(sendButton);

        return input;
    }
}
