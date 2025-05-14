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

    public ChatBubblePanel(String username, String message, UserIconButton userIconButton) {
        // 전체: 가로 박스 레이아웃
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);

        // 이름 + 말풍선 컨테이너
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentY(Component.TOP_ALIGNMENT);

        // 이름
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 10));
        nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(0, 0, 0, PADDING / 2));
        content.add(nameLabel);

        // 말풍선과 대화 사이 빈 공간 생성
        content.add(Box.createRigidArea(new Dimension(0, PADDING / 2)));

        // 말풍선
        JPanel bubble = new RoundedPanel(10);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.X_AXIS));
        bubble.setBackground(Color.decode("#D9D9D9"));
        bubble.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        bubble.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // 말풍선 내 대화
        JTextArea msgArea = new JTextArea(message);
        msgArea.setLineWrap(true);                // 줄바꿈 허용
        msgArea.setWrapStyleWord(true);           // 단어 단위로 줄바꿈
        msgArea.setEditable(false);               // 편집 불가
        msgArea.setOpaque(false);                 // 배경 투명
        msgArea.setBorder(null);                  // 테두리 제거
        msgArea.setFont(new Font("Pretendard", Font.PLAIN, 12));

        FontMetrics fm = msgArea.getFontMetrics(msgArea.getFont());
        int actualWidth = fm.stringWidth(message);

        final int MAX_WIDTH = 170;

        msgArea.setSize(new Dimension(MAX_WIDTH, Short.MAX_VALUE));
        Dimension preferred = msgArea.getPreferredSize();

        int textWidth = (actualWidth <= MAX_WIDTH) ? textWidth = actualWidth + PADDING - 2 : MAX_WIDTH;

        Dimension adjusted = new Dimension(textWidth, preferred.height);

        // 크기 제한 적용
        msgArea.setMinimumSize(adjusted);
        msgArea.setPreferredSize(adjusted);
        msgArea.setMaximumSize(adjusted);

        // 말풍선 크기도 adjusted 기반으로
        int bubbleW = adjusted.width + PADDING * 2;
        int bubbleH = adjusted.height + PADDING * 2;
        bubble.setMinimumSize(new Dimension(bubbleW, bubbleH));
        bubble.setPreferredSize(new Dimension(bubbleW, bubbleH));
        bubble.setMaximumSize(new Dimension(bubbleW, bubbleH));

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
        add(content);
        add(Box.createRigidArea(new Dimension(PADDING, 0)));
        add(buttonPanel);
    }
}