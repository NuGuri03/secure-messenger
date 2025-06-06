package client.ui;

import client.ChatClient;
import client.ResourceCache;
import client.WindowManager;
import networked.UserInfo;

import java.awt.*;
import javax.swing.*;

public class ProfileUI extends BaseUI {
    public ProfileUI(ChatClient client, UserInfo user) {
    	super(client);

        setSize(400, 700);
        setMinimumSize(new Dimension(400, 700));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        Font mainFont = new Font("Pretendard", Font.PLAIN, 16);
        Font subFont = new Font("Pretendard", Font.PLAIN, 14);
        Font BioFont = new Font("Pretendard Light", Font.PLAIN, 13);
        
        // 프로필 사진
        JLabel profileLabel = new JLabel(ResourceCache.getIcon("/images/default_profile.png", 100));
        
        profileLabel.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(profileLabel, gbc);
        
        
        // 배경사진
        JLabel BackLabel = new JLabel(ResourceCache.getIcon("/images/default_background.png", 400));

        gbc.gridy = 0;
        gbc.gridheight = 3; // 0~3행 병합
        gbc.anchor = GridBagConstraints.NORTH;
        add(BackLabel, gbc);
        
        // 프로필 이름
        JLabel labelID = new JLabel(user.getUsername());
        labelID.setFont(mainFont);
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 1, 10);
        add(labelID, gbc);

        // 자기소개
        JLabel BioID = new JLabel(user.getBio());
        BioID.setFont(BioFont);
        gbc.gridy = 5;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 10, 10, 10);
        add(BioID, gbc);
        
        
        // 채팅 버튼
        JButton btnChat = new JButton("1:1 채팅");
        btnChat.setFont(subFont);
        gbc.gridy = 6;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(10, 10, 10, 10);  
        add(btnChat, gbc);

        boolean roomExists = client.getPrivateRoomInfo(user.getId()) != null;
        if (!roomExists) {
            // Create a new private room right away if it doesn't exist
            String roomName = "";
            client.createRoom(roomName, user.getHandle());
        }

        btnChat.addActionListener(
            e -> {
                var roomInfo = client.getPrivateRoomInfo(user.getId());
                if (roomInfo == null) { return; }

                WindowManager.openChatUI(roomInfo);
                dispose();
            }
        );
        
        setVisible(true);
    }
}
