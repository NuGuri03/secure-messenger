package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.formdev.flatlaf.FlatLightLaf;

public class ChatUI extends JFrame {
    private String username;

    public ChatUI(String username) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            Font customFont = new Font("Pretendard", Font.PLAIN, 14);
            UIManager.put("defaultFont", customFont);


        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        if (username == null || username.trim().isEmpty()) {
            username = "user";
        }
        this.username = username;

        setTitle("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension minSize = new Dimension(450, 600);
        this.setSize(minSize);
        this.setMinimumSize(minSize);

        this.setLayout(new BorderLayout());

        // 사이드바 패널
        JPanel sidebar = createSidebarPanel();

        // 채팅창 패널
        JPanel chatPanel = createChatPanel();

        // 탑바 영역
        JPanel topbar = createTopbarPanel(username);

        // 채팅 내용 출력 영역
        JPanel chatArea = createChatArea();
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 메세지 입력창 영역
        JPanel inputPanel = createInputPanel(chatArea, scrollPane, username);

        // 위치 설정
        chatPanel.add(topbar, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 주어진 이미지 경로로 아이콘 버튼을 생성
     * @param path 이미지 경로 (예: "resources/icon/icon.png")
     * @param size 아이콘 크기 (예: 32 → 32x32)
     * @param toolTip 아이콘 툴팁 (예: icon)
     * @return 스타일이 적용된 JButton
     */
    private JButton createIconButton(String path, int size, String toolTip) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setPreferredSize(new Dimension(size, size));
        button.setMinimumSize(new Dimension(size, size));
        button.setMaximumSize(new Dimension(size, size));

        button.setToolTipText(toolTip);
        button.setContentAreaFilled(false);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * 사이드바 패널 생성
     * @return 사이드바 패널
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(80, 0));  // 너비 고정

        // 정렬 및 여백
        sidebar.setAlignmentY(Component.TOP_ALIGNMENT);
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 5, 30, 5));

        // 배경
        sidebar.setBackground(Color.decode("#A9A9A9"));

        JButton userButton = createIconButton("/icon/user.png", 24, "User");
        JButton chatButton = createIconButton("/icon/chat.png", 20, "Chat");
        JButton settingsButton = createIconButton("/icon/settings.png", 20, "Settings");

        sidebar.add(Box.createVerticalStrut(30)); // 간격
        sidebar.add(userButton);
        sidebar.add(Box.createVerticalStrut(40)); // 간격
        sidebar.add(chatButton);
        sidebar.add(Box.createVerticalStrut(40)); // 간격
        sidebar.add(settingsButton);

        return sidebar;
    }

    /**
     * 탑바 패널을 생성
     * @param username 유저 네임
     * @return 문구가 포함된 패널
     */
    private JPanel createTopbarPanel(String username) {
        JPanel topbar = new JPanel();

        topbar.setBackground(new Color(175, 175, 175));

        // 유저 아이콘 버튼
        UserIconButton userIconButton = new UserIconButton("/icon/default_profile.png", 38);

        // 유저 이름 라벨
        JLabel usernameLabel = new JLabel(String.format("Chatting with %s", username));

        // 탑바에 추가
        topbar.add(Box.createHorizontalStrut(15)); // 왼쪽 여백
        topbar.add(userIconButton);
        topbar.add(Box.createHorizontalStrut(12)); // 아이콘과 라벨 사이 여백
        topbar.add(usernameLabel);

        // 설정
        topbar.setPreferredSize(new Dimension(0, 50)); // 높이를 50px로 설정
        topbar.setLayout(new BoxLayout(topbar, BoxLayout.X_AXIS));
        topbar.setAlignmentY(Component.TOP_ALIGNMENT);

        return topbar;
    }

    /**
     * 채팅 로그 패널 생성
     * @return 채팅 로그 패널
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        return chatPanel;
    }

    /**
     * 채팅창 텍스트 영역 생성
     * @return 채팅창 텍스트 영역
     */
    private JPanel createChatArea() {
        JPanel chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return chatArea;    }

    /**
     * 채팅창 입력 패널 생성
     * @param chatArea 채팅창 텍스트 영역
     * @param username 유저 네임
     * @return 채팅창 입력 패널
     */
    private JPanel createInputPanel(JPanel chatArea, JScrollPane scrollPane, String username) {

        // 메세지 입력창
        JTextArea inputArea = new JTextArea();
        inputArea.setEditable(true);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

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
            @Override
            public void focusLost(FocusEvent e) {
                if (inputArea.getText().trim().isEmpty()) {
                    inputArea.setText(placeholder);
                    inputArea.setForeground(Color.GRAY);
                }
            }
        });

        // 전송 버튼
        JButton sendButton = new JButton("전송");
        sendButton.setPreferredSize(new Dimension(70, 28)); // width=50, height=28

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

            if (message == null || message.trim().isEmpty() || message.equals(placeholder)) return;

            ChatBubblePanel bubble = new ChatBubblePanel(username, message, "/icon/default_profile.png");
            bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
            chatArea.add(bubble);
            chatArea.add(Box.createVerticalStrut(10));

            chatArea.revalidate();
            chatArea.repaint();

            inputArea.setText("");

            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });

        // 버튼을 오른쪽에 정렬하기 위한 래퍼 패널
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.add(sendButton);
        buttonWrapper.setOpaque(false); // 배경 투명

        // 패널 생성
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        inputPanel.add(inputArea);
        inputPanel.add(Box.createVerticalStrut(5)); // 입력창-버튼 간 간격
        inputPanel.add(buttonWrapper);

        return inputPanel;
    }
}