package client.ui.panel;

import client.ChatClient;
import client.ui.component.panel.ChatInfoPanel;
import client.ui.component.panel.UserInfoPanel;
import networked.RoomInfo;
import networked.UserInfo;
import server.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class RecentChatPanel extends JPanel {
    private static final int PADDING = 30;

    public RecentChatPanel(ChatClient client) {
        setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("나의 최근 채팅");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(PADDING / 6, 0, PADDING / 6, 0));
        title.add(titleLabel);
        title.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));
        add(title, BorderLayout.NORTH);


        JPanel chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chatListPanel.setBorder(BorderFactory.createEmptyBorder(PADDING / 2, PADDING / 2, PADDING / 2, PADDING / 2));

        ArrayList<ChatInfoPanel> recentChatList = new ArrayList<>();

        //sort rooms by last message time (newest first)
        List<Map.Entry<RoomInfo, List<UserInfo>>> ordered =
                client.rooms.entrySet().stream()
                        .sorted((a, b) -> Long.compare(
                                b.getKey().getLastMessageTimestamp(),
                                a.getKey().getLastMessageTimestamp()))
                        .toList();

        //build recentChatList in that order
        for (Map.Entry<RoomInfo, List<UserInfo>> entry : ordered) {
            RoomInfo room = entry.getKey();
            List<UserInfo> users = entry.getValue();
            String myHdl = client.getUserInfo().getHandle();

            List<UserInfo> others = users.stream()
                    .filter(u -> !u.getHandle().equals(myHdl))
                    .toList();

            //self-room
            if (others.isEmpty()) {
                String name = "(나) — " + client.getUserInfo().getUsername();
                recentChatList.add(new ChatInfoPanel(room, name, client.getUserInfo()));
                continue;
            }

            //private chat: two members, room name empty
            if (others.size() == 1 && room.getName().isEmpty()) {
                UserInfo friend = others.get(0);
                recentChatList.add(new ChatInfoPanel(room, friend.getUsername(), friend));
                continue;
            }

            //group chat: everything else
            String name = room.getName() + " — " + room.getMemberHandles().length;
            recentChatList.add(new ChatInfoPanel(room, name, null));
        }

        for (var chatInfoPanel : recentChatList) {
            chatListPanel.add(chatInfoPanel);
            chatListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between panels
        }

        JScrollPane scrollPane = new JScrollPane(chatListPanel);
        scrollPane.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#A9A9A9")));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        //create groupchat ui
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING / 2, PADDING / 2));
        south.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#A9A9A9")));

        JButton addGroupBtn = new JButton("+");
        addGroupBtn.setPreferredSize(new Dimension(32, 32));
        addGroupBtn.setFocusable(false);
        addGroupBtn.setToolTipText("새 그룹 만들기 / Crear grupo");
        addGroupBtn.addActionListener(e -> showCreateGroupDialog(client));   // ②
        south.add(addGroupBtn);

        add(south, BorderLayout.SOUTH);
    }

    private void showCreateGroupDialog(ChatClient client) {

        //constants for tile height and colors
        final int TILE_H = 56;
        final Color SELECT_BG = new Color(0xD2E6FC);
        final Color DEFAULT_BG = UIManager.getColor("Panel.background");

        //create the modal dialog
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, "그룹 만들기", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(400, 520);
        dlg.setLocationRelativeTo(owner);

        //root panel with padding
        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        dlg.setContentPane(root);

        //group name field
        JPanel namePanel = new JPanel(new BorderLayout(6, 0));
        JLabel nameLbl = new JLabel("그룹 이름:");
        nameLbl.setFont(new Font("Pretendard", Font.PLAIN, 14));
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Pretendard", Font.PLAIN, 14));
        namePanel.add(nameLbl, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        root.add(namePanel, BorderLayout.NORTH);

        //label above friends list
        JLabel hint = new JLabel("그룹 인원 선택");
        hint.setFont(new Font("Pretendard", Font.PLAIN, 12));

        //panel holding all user rows
        JPanel friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

        //load friends and track selected ones
        List<UserInfo> friends = client.getFriendList();
        Set<UserInfo> selected = new HashSet<>();

        for (UserInfo friend : friends) {

            //panel showing a single user
            UserInfoPanel core = new UserInfoPanel(client, friend);
            core.setOpaque(true);
            core.setBackground(DEFAULT_BG);

            //remove default mouse listeners so it does nothing
            removeAllMouseListeners(core);

            //checkmark shown when selected
            JLabel check = new JLabel("✓", SwingConstants.CENTER);
            check.setPreferredSize(new Dimension(26, TILE_H));
            check.setFont(new Font("Pretendard", Font.BOLD, 14));
            check.setVisible(false);

            //row container with cursor hand
            JPanel row = new JPanel(new BorderLayout());
            row.add(core, BorderLayout.CENTER);
            row.add(check, BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, TILE_H));
            row.setPreferredSize(new Dimension(0, TILE_H));
            row.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            row.setBackground(DEFAULT_BG);
            row.setOpaque(true);
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            //toggle listener to select or deselect user
            MouseAdapter toggle = new MouseAdapter() {
                private void updateVisual(boolean sel) {
                    Color bg = sel ? SELECT_BG : DEFAULT_BG;
                    row.setBackground(bg);
                    core.setBackground(bg);
                    check.setVisible(sel);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean sel = selected.contains(friend);
                    if (sel) selected.remove(friend);
                    else selected.add(friend);
                    updateVisual(!sel);
                }
            };

            //attach toggle listener to row and its children
            addMouseListenerRecursively(row, toggle);

            friendsPanel.add(row);
        }

        //scroll pane around friends list
        JScrollPane scroll = new JScrollPane(friendsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //visible border on all sides
        scroll.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#A9A9A9")));

        //wrapper for hint + scroll
        JPanel listWrapper = new JPanel(new BorderLayout(0, 5));
        listWrapper.add(hint, BorderLayout.NORTH);
        listWrapper.add(scroll, BorderLayout.CENTER);
        root.add(listWrapper, BorderLayout.CENTER);

        //create button
        JButton createBtn = new JButton("생성 / Create");
        createBtn.setFont(new Font("Pretendard", Font.PLAIN, 13));

        //validate input then create room
        createBtn.addActionListener(ev -> {
            String groupName = nameField.getText().trim();
            if (groupName.length() < 2 || groupName.length() > 16) {
                JOptionPane.showMessageDialog(dlg, "그룹 이름은 1~16자로 작성하세요");
                return;
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "한 명 이상의 친구를 선택하세요.");
                return;
            }
            String[] memberHandles = selected.stream()
                    .map(UserInfo::getHandle)
                    .toArray(String[]::new);
            client.createRoom(groupName, memberHandles);
            dlg.dispose();
        });

        //south panel for the button
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(createBtn);
        root.add(south, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }


    private static void removeAllMouseListeners(Component c) {
        for (MouseListener ml : c.getMouseListeners()) c.removeMouseListener(ml);
        if (c instanceof Container cont)
            for (Component child : cont.getComponents()) removeAllMouseListeners(child);
    }

    private static void addMouseListenerRecursively(Component c, MouseListener l) {
        c.addMouseListener(l);
        if (c instanceof Container cont)
            for (Component child : cont.getComponents()) addMouseListenerRecursively(child, l);
    }


}
