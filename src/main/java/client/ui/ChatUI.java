package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import client.ui.component.panel.ChatBubblePanel;
import networked.RoomInfo;
import client.ChatClient;
import client.ui.component.button.UserIconButton;

public class ChatUI extends BaseUI {
    private ChatClient client;
    private RoomInfo roomInfo;

    public ChatUI(ChatClient client, RoomInfo roomInfo) {
        super(client);
        this.client = client;
        this.roomInfo = roomInfo;

        String myUsername = client.getCurrentUser().getUsername();

        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension minSize = new Dimension(450, 600);
        this.setSize(minSize);
        this.setMinimumSize(minSize);

        this.setLayout(new BorderLayout());

        // 유저 아이콘 버튼
        UserIconButton myAvatar = new UserIconButton(client.getUserInfo(), 32);

        // 탑바 영역
        JPanel topbar = createTopbarPanel();

        // 채팅창 패널
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // 채팅 내용 출력 영역
        JPanel chatArea = createChatArea();
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 메세지 입력창 영역
        JPanel inputPanel = createInputPanel(chatArea, scrollPane, myUsername, myAvatar);

        // 위치 설정
        chatPanel.add(topbar, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    /**
     * 탑바 패널을 생성
     * @return 문구가 포함된 패널
     */
    private JPanel createTopbarPanel() {
        JPanel topbar = new JPanel();

        // 설정
        topbar.setLayout(new BoxLayout(topbar, BoxLayout.X_AXIS));
        topbar.setPreferredSize(new Dimension(0, 60)); // 높이를 50px로 설정
        topbar.setAlignmentY(Component.TOP_ALIGNMENT);
        topbar.setBackground(new Color(175, 175, 175));

        // 상대방 아이콘 버튼
        UserIconButton userIconButton = new UserIconButton(client.getUserInfo(), 32);

        // 채팅방 이름 라벨
        JLabel usernameLabel = new JLabel(roomInfo.getName());

        // 탑바 패널에 요소 추가
        topbar.add(Box.createHorizontalStrut(15)); // 왼쪽 여백
        topbar.add(userIconButton);
        topbar.add(Box.createHorizontalStrut(12)); // 아이콘과 라벨 사이 여백
        topbar.add(usernameLabel);

        return topbar;
    }

    /**
     * 채팅창 텍스트 영역 생성
     * @return 채팅창 텍스트 영역
     */
    private JPanel createChatArea() {
        JPanel chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return chatArea;
    }

    /**
     * 채팅창 입력 패널 생성
     * @param chatArea 채팅창 텍스트 영역
     * @param username 유저 네임
     * @return 채팅창 입력 패널
     */
    private JPanel createInputPanel(JPanel chatArea, JScrollPane scrollPane, String username, UserIconButton userIconButton) {
        JTextArea inputArea = createInputArea();

        JButton sendButton = new JButton("전송");
        sendButton.setPreferredSize(new Dimension(70, 28));

        JButton sendTestButton = new JButton("Test");
        sendTestButton.setPreferredSize(new Dimension(70, 28));

        // 전송 버튼 클릭 시
        sendButton.addActionListener(e -> {
            handleSend(chatArea, scrollPane, inputArea, username, userIconButton, true);
        });

        sendTestButton.addActionListener(e -> {
            handleSend(chatArea, scrollPane, inputArea, "Test", userIconButton, false);
        });

        // 엔터 입력 시 전송
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendButton.doClick();
                }
            }
        });

        // 버튼을 오른쪽 정렬
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.add(sendTestButton);
        buttonWrapper.add(sendButton);
        buttonWrapper.setOpaque(false);

        // 패널 구성
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        inputPanel.add(inputArea);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(buttonWrapper);

        return inputPanel;
    }

    private JTextArea createInputArea() {
        String placeholder = "메세지 입력";

        JTextArea inputArea = new JTextArea();
        inputArea.setEditable(true);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        inputArea.setText(placeholder);
        inputArea.setForeground(Color.GRAY);

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

        return inputArea;
    }

    private void handleSend(JPanel chatArea, JScrollPane scrollPane, JTextArea inputArea,
                            String sender, UserIconButton userIconButton, boolean isRight) {
        String message = inputArea.getText().trim();
        if (message.isEmpty() || message.equals("메세지 입력")) return;

        JPanel bubble = new ChatBubblePanel(sender, message, userIconButton, isRight);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        Dimension preferredSize = bubble.getPreferredSize();
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));

        if (isRight) {
            wrapper.add(bubble, BorderLayout.EAST);
        } else {
            wrapper.add(bubble, BorderLayout.WEST);
        }

        chatArea.add(wrapper);
        chatArea.add(Box.createVerticalStrut(10));
        inputArea.setText("");

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
}