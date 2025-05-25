package client.ui;

import java.awt.*;
import javax.swing.*;

public class ProfileUI extends BaseUI{
    private static final long serialVersionUID = 1L;
    public ProfileUI() 
    {
    	super();

        setTitle("환영합니다");
        setSize(400, 300);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        //Font mainFont = new Font("Pretendard", Font.PLAIN, 16);
        Font subFont = new Font("Pretendard", Font.PLAIN, 14);
        
        JButton btnChat = new JButton("1 : 1 채팅");
        btnChat.setFont(subFont);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;

        add(btnChat, gbc);
        
        btnChat.addActionListener(e->{
        	new ChatUI("홍길동");
        });
        
        setVisible(true);

    }
}
