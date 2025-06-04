package client.ui.component.panel;

import client.ui.component.button.UserIconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 하나의 채팅 메시지(아바타 + 이름 + 말풍선)를 표시하는 패널
 */
public class ChatBubblePanel extends JPanel {
    private static final int PADDING = 8;

    public ChatBubblePanel(String username, String message, UserIconButton userIconButton, boolean isLeft) {
        // 전체: 가로 박스 레이아웃
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // 이름 + 말풍선 컨테이너
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        // 이름
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 10));

        if (isLeft) {
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameLabel.setBorder(new EmptyBorder(PADDING / 2, 0, 0, 0));
        } else {
            nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            nameLabel.setBorder(new EmptyBorder(0, 0, 0, PADDING / 2));
        }
        content.add(nameLabel);

        // 말풍선과 대화 사이 빈 공간 생성
        content.add(Box.createRigidArea(new Dimension(0, PADDING / 2)));

        // 말풍선
        JPanel bubble = new RoundedPanel(10);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.X_AXIS));
        bubble.setBackground(Color.decode("#D9D9D9"));
        bubble.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        if (isLeft) {
            bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
        } else {
            bubble.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        // 말풍선 내 대화칸
        JTextArea msgArea = new JTextArea(message);
        msgArea.setLineWrap(false);
        msgArea.setWrapStyleWord(false);
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setFont(new Font("Pretendard", Font.PLAIN, 13));
        msgArea.setMargin(new Insets(0,0,0,0));

        // 한 줄로 놓았을 때의 크기 측정
        Dimension oneLine = msgArea.getPreferredSize();

        final int MAX_WIDTH = 170;

        // 실제 줄바꿈이 필요한지 검사
        boolean needWrap = oneLine.width > MAX_WIDTH;
        msgArea.setLineWrap(needWrap);
        msgArea.setWrapStyleWord(needWrap);

        // 폭 설정:
        //    - wrap이 필요 없으면 실제 한 줄 폭
        //    - 필요하면 MAX_WIDTH
        int finalWidth = needWrap ? MAX_WIDTH : oneLine.width;

        // 높이는 wrap 상태에서 다시 계산
        msgArea.setSize(new Dimension(finalWidth, Short.MAX_VALUE));
        Dimension wrapped = msgArea.getPreferredSize();

        // 사이즈 고정
        msgArea.setMinimumSize(wrapped);
        msgArea.setPreferredSize(wrapped);
        msgArea.setMaximumSize(wrapped);

        // 말풍선에 대화 추가
        bubble.add(msgArea);
        content.add(bubble);

        // 유저 아이콘 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(PADDING, 0, 0, 0));

        // 패널에 유저 아이콘 버튼 추가
        UserIconButton copiedUserIconButton = userIconButton.copy();
        copiedUserIconButton.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonPanel.add(copiedUserIconButton);

        // 완성
        if (isLeft) {
            add(buttonPanel);
            add(Box.createRigidArea(new Dimension(PADDING, 0)));
            add(content);
        } else {
            add(content);
            add(Box.createRigidArea(new Dimension(PADDING, 0)));
            add(buttonPanel);
        }
    }
}
