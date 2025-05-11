package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 하나의 채팅 메시지(아바타 + 이름 + 말풍선)를 표시하는 패널.
 * 말풍선 크기는 메시지 텍스트 길이에만 종속됩니다.
 */
public class ChatBubblePanel extends JPanel {
    private static final int AVATAR_SIZE = 36;
    private static final int PADDING = 8;

    public ChatBubblePanel(String username, String message, String avatarPath) {
        // 전체: 가로 박스 레이아웃
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);

        // 아바타
        UserIconButton avatar = new UserIconButton(avatarPath, AVATAR_SIZE);
        avatar.setAlignmentY(Component.CENTER_ALIGNMENT);
        add(avatar);

        add(Box.createRigidArea(new Dimension(8, 0)));

        // 이름 + 말풍선 컨테이너
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 이름
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Pretendard", Font.BOLD, 10));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nameLabel);

        content.add(Box.createRigidArea(new Dimension(0, 4)));

        // 말풍선
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.X_AXIS));
        bubble.setBackground(Color.decode("#D9D9D9"));
        bubble.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        bubble.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Pretendard", Font.PLAIN, 12));
        msgArea.setLineWrap(true);                 // 줄바꿈 허용
        msgArea.setWrapStyleWord(true);           // 단어 단위로 줄바꿈
        msgArea.setEditable(false);               // 편집 불가
        msgArea.setOpaque(false);                 // 배경 투명
        msgArea.setBorder(null);                  // 테두리 제거

        msgArea.setSize(new Dimension(200, Short.MAX_VALUE));  // 너비 제한 후 높이 자동 계산
        Dimension preferred = msgArea.getPreferredSize();
        msgArea.setMaximumSize(preferred);

        // 3. 말풍선에 추가
        bubble.add(msgArea);
        bubble.setMaximumSize(new Dimension(preferred.width + PADDING * 2, preferred.height + PADDING * 2));

        content.add(bubble);
        add(content);
    }
}
