package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import client.ui.component.panel.ChatBubblePanel;
import networked.RoomInfo;
import client.ChatClient;
import client.ui.component.button.UserIconButton;
import networked.UserInfo;

public class ChatUI extends BaseUI {
    private ChatClient client;
    public RoomInfo roomInfo;

    private JPanel chatArea;
    private JScrollPane scrollPane;


    public ChatUI(ChatClient client, RoomInfo roomInfo) {
        super(client);
        this.client = client;
        this.roomInfo = roomInfo;

       updateUI();
    }

    public void updateUI()
    {
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension minSize = new Dimension(450, 600);
        this.setSize(minSize);
        this.setMinimumSize(minSize);

        this.setLayout(new BorderLayout());

        // 유저 아이콘 버튼
        new UserIconButton(client.getUserInfo(), 32);

        // 탑바 영역
        JPanel topbar = createTopbarPanel();

        // 채팅창 패널
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // 채팅 내용 출력 영역
        JPanel chatArea = createChatArea();
        scrollPane = new JScrollPane(chatArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 메세지 입력창 영역
        JPanel inputPanel = createInputPanel();

        // 위치 설정
        chatPanel.add(topbar, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        loadHistory();

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
        UserIconButton userIconButton = null;
        String roomName = roomInfo.getName();
        if (roomInfo.getMemberHandles().length == 1)
        {
            userIconButton = new UserIconButton(client.getUserInfo(), 32);
            roomName = client.getUserInfo().getUsername();
        }
        else if (roomInfo.getMemberHandles().length == 2)
        {
            for(String handle : roomInfo.getMemberHandles())
            {
                if(!handle.equals(client.getCurrentUser().getHandle()))
                {
                    UserInfo friend = client.getFriendList().stream()
                            .filter(u -> u.getHandle().equals(handle))
                            .findFirst()
                            .orElse(new UserInfo(-1, handle, "", "", "", null));

                    userIconButton = new UserIconButton(friend, 32);
                    roomName = friend.getUsername();
                }
            }

        }


        // 채팅방 이름 라벨
        JLabel usernameLabel = new JLabel(roomName);

        // 탑바 패널에 요소 추가
        topbar.add(Box.createHorizontalStrut(15)); // 왼쪽 여백
        if(userIconButton != null) topbar.add(userIconButton); //group chat doesnt have user icon
        topbar.add(Box.createHorizontalStrut(12)); // 아이콘과 라벨 사이 여백
        topbar.add(usernameLabel);

        return topbar;
    }

    // 채팅창 텍스트 영역 생성
    private JPanel createChatArea() {
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return chatArea;
    }

    // 채팅창 입력 패널
    private JPanel createInputPanel() {
        JTextArea inputArea = createInputArea();

        JButton sendButton = new JButton("전송");
        sendButton.setPreferredSize(new Dimension(70, 28));


        // 전송 버튼 클릭 시
        sendButton.addActionListener(e -> {
            handleSend(inputArea);
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

    // 메시지 입력창 생성
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

    private void handleSend(JTextArea inputArea) {
        String message = inputArea.getText().trim();
        if (message.isEmpty() || message.equals("메세지 입력")) return;

        client.sendMessage(roomInfo.getId(), message); //send to server

        inputArea.setText("");
    }

    private void loadHistory() {
        for (RoomInfo.Message m : roomInfo.getMessages()) {
            boolean isMe = m.authorHandle().equals(client.getCurrentUser().getHandle());
            UserIconButton avatar = new UserIconButton(
                    isMe ? client.getUserInfo()
                            : client.getFriendList().stream()
                            .filter(u -> u.getHandle().equals(m.authorHandle()))
                            .findFirst()
                            .orElse(new UserInfo(-1, m.authorHandle(),"","","",null)),
                    32);

            String sendName = isMe ? client.getUserInfo().getUsername() : client.getFriendList().stream()
                    .filter(u -> u.getHandle().equals(m.authorHandle()))
                    .findFirst()
                    .map(UserInfo::getUsername)
                    .orElse(m.authorHandle());

            addBubble(sendName, avatar, m.plainText(), !isMe);
        }
    }

    private void addBubble(String sender, UserIconButton avatar, String text, boolean left) {

        JPanel bubble = new ChatBubblePanel(sender, text, avatar, left);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, bubble.getPreferredSize().height));

        wrapper.add(bubble, left ? BorderLayout.WEST : BorderLayout.EAST);
        chatArea.add(wrapper);
        chatArea.add(Box.createVerticalStrut(10));

        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        });
    }

    public void appendIncoming(RoomInfo.Message m) {
        boolean isLeft = !m.authorHandle().equals(client.getCurrentUser().getHandle());
        UserIconButton avatar = new UserIconButton(
                !isLeft ? client.getUserInfo()
                        : client.getFriendList().stream()
                        .filter(u -> u.getHandle().equals(m.authorHandle()))
                        .findFirst()
                        .orElse(new UserInfo(-1, m.authorHandle(),"","","",null)),
                32);

        String sendName = !isLeft ? client.getUserInfo().getUsername() : client.getFriendList().stream()
                .filter(u -> u.getHandle().equals(m.authorHandle()))
                .findFirst()
                .map(UserInfo::getUsername)
                .orElse(m.authorHandle());

        addBubble(sendName, avatar, m.plainText(), isLeft);

        chatArea.revalidate();
        chatArea.repaint();
    }
}