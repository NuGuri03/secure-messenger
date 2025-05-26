package client.ui;

import java.awt.*;
import javax.swing.*;

public class ProfileUI extends BaseUI {
    public ProfileUI(String username) {
    	super();

        setSize(400, 700);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        Font mainFont = new Font("Pretendard", Font.PLAIN, 16);
        Font subFont = new Font("Pretendard", Font.PLAIN, 14);
        
        // 프로필 사진
        ImageIcon profileIcon = new ImageIcon(getClass().getResource("/images/default_profile.png"));
        Image scaledImage = profileIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel profileLabel = new JLabel(new ImageIcon(scaledImage));
        
        profileLabel.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(profileLabel, gbc);
        
        
        // 배경사진

        ImageIcon BackIcon = new ImageIcon(getClass().getResource("/images/default_background.png"));
        Image BackscaledImage = BackIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        JLabel BackLabel = new JLabel(new ImageIcon(BackscaledImage));

        gbc.gridy = 0;
        gbc.gridheight = 3; // 0~3행 병합
        gbc.anchor = GridBagConstraints.CENTER;
        add(BackLabel, gbc);
        
        // 프로필 이름
        JLabel labelID = new JLabel(username);
        labelID.setFont(mainFont);
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(labelID, gbc);

        
        
        // 채팅 버튼
        JButton btnChat = new JButton("1:1 채팅");
        btnChat.setFont(subFont);
     
        gbc.gridy = 5;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        
        add(btnChat, gbc);
        
        btnChat.addActionListener(e->{
        	new ChatUI(username);
        });
        
        setVisible(true);

    }
}
