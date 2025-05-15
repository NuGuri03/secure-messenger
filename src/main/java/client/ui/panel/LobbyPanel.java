package client.ui.panel;

import client.ui.component.panel.UserInfoPanel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.Collator;
import java.util.Comparator;
import java.util.Vector;

public class LobbyPanel extends JPanel {
    public LobbyPanel() {
        setLayout(new BorderLayout());

        JPanel title = new JPanel();
        JLabel titleLabel = new JLabel("나의 로비");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));
        add(title, BorderLayout.NORTH);

        // 유저 패널
        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.PAGE_AXIS));
        usersPanel.setBorder(null);
        usersPanel.add(Box.createVerticalStrut(30));

        UserInfoPanel myInfo = new UserInfoPanel(null, null, null);
        myInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        usersPanel.add(myInfo);

        usersPanel.add(Box.createVerticalStrut(30));

        // "나" 와 "친구" 간의 구분선
        JPanel line = new JPanel();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(0, 1));
        line.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#A9A9A9")));
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        usersPanel.add(line);

        usersPanel.add(Box.createVerticalStrut(30));

        Vector<UserInfoPanel> userInfo = new Vector<UserInfoPanel>();
        // 예시 유저
        userInfo.add(new UserInfoPanel("호반우", "KNU CSE", null));
        userInfo.add(new UserInfoPanel("김민준", "코딩을 사랑합니다 저를 굴려주세요 PM님 힝힝 (당근)", null));
        userInfo.add(new UserInfoPanel("장기원", "KERT 들어와 주세요 힝힝", null));
        userInfo.add(new UserInfoPanel("서유민", "PM", null));
        userInfo.add(new UserInfoPanel("Bruno", "떼굴떼굴 구르는 중...", null));
        userInfo.add(new UserInfoPanel("정성진", "떼굴떼굴 구르는 중...", null));
        userInfo.add(new UserInfoPanel("권혁주", "Login UI 만드는 중...", null));
        userInfo.add(new UserInfoPanel("신승빈", "SignUp UI 만드는 중...", null));

        // 이름 가나다 순서
        userInfo.sort(Comparator.comparing(user -> user.getUsername(), Collator.getInstance()));

//        int friendCount = userInfo.size(); // 미완성

        for (UserInfoPanel user : userInfo) {
            user.setAlignmentX(Component.LEFT_ALIGNMENT);
            usersPanel.add(user);
            usersPanel.add(Box.createVerticalStrut(40));
        }

        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#A9A9A9")));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }
}
