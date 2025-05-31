package client.ui.panel;

import client.ChatClient;
import client.ui.component.panel.UserInfoPanel;
import networked.UserInfo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;

public class LobbyPanel extends JPanel {
    private JPanel usersPanel;
    private ArrayList<UserInfoPanel> friendList;
    private static final int PADDING = 30;

    public LobbyPanel(ChatClient client) {
        setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("나의 로비");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));

        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));
        add(title, BorderLayout.NORTH);

        usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.PAGE_AXIS));
        usersPanel.setBorder(null);

        buildUserList(client);

        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#A9A9A9")));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void buildUserList(ChatClient client) {
        usersPanel.removeAll();

        // 유저 패널
        usersPanel.add(Box.createVerticalStrut(PADDING));

        UserInfoPanel myInfoPanel = new UserInfoPanel(client.getUserInfo());
        myInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usersPanel.add(myInfoPanel);

        usersPanel.add(Box.createVerticalStrut(30));

        // "나" 와 "친구" 간의 구분선
        JPanel line = new JPanel();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(0, 1));
        line.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#A9A9A9")));
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        usersPanel.add(line);

        friendList = new ArrayList<>();
        // 예시 유저
        // **myInfo.getPublicKey()** 는 나중에 삭제하여야 함
        friendList.add(new UserInfoPanel(new UserInfo("user1", "호반우", "KNU CSE", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user2", "김민준", "코딩을 사랑합니다 저를 굴려주세요 PM님 힝힝 (당근)", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user3", "장기원", "KERT 들어와 주세요 힝힝", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user4", "서유민", "PM", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user5", "Bruno", "백엔드 구축 중...", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user6", "정성진", "백엔드 만드는 중...", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user7", "권혁주", "로그인, 프로필 UI 만드는 중...", null, null)));
        friendList.add(new UserInfoPanel(new UserInfo("user8", "신승빈", "회원가입, 설정 UI 만드는 중...", null, null)));

        // 이름 가나다 순서로 정렬
        friendList.sort(Comparator.comparing(
                panel -> panel.getUserInfo().getUsername(),
                Collator.getInstance()
        ));

        // 친구 수 표시
        int friendCount = friendList.size();
        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        friendPanel.add(Box.createVerticalStrut(PADDING * 2 / 3));

        JLabel friendLabel = new JLabel(String.format("친구 %d명", friendCount));
        friendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        friendLabel.setFont(new Font("Pretendard", Font.PLAIN, 12));

        friendPanel.setBorder(BorderFactory.createEmptyBorder(0, PADDING * 4 / 3, 0, 0));
        friendPanel.add(friendLabel);

        usersPanel.add(friendPanel);

        usersPanel.add(Box.createVerticalStrut(PADDING * 2 / 3));

        // 친구 목록
        for (UserInfoPanel user : friendList) {
            user.setAlignmentX(Component.LEFT_ALIGNMENT);
            usersPanel.add(user);
            usersPanel.add(Box.createVerticalStrut(PADDING));
        }
    }
}
