package org.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LockScreen {
    private static final String PASSWORD = "12345";
    private static JFrame frame;
    private static Process process = null;

    public static void lockScreen() throws IOException {
        frame = new JFrame("Locked");
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel panel = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground( new Color(255, 0, 0, 20) );

        panel.setLayout(new GridBagLayout());
        JLabel label = new JLabel("Enter Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton unlockButton = new JButton("Unlock");
        process = Runtime.getRuntime().exec("Client/blockkey.exe");

        unlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputPassword = "12345";
                if (inputPassword.equals(PASSWORD)) {
                    frame.dispose();
                    process.destroy();
                } else {
                    JOptionPane.showMessageDialog(frame, "Wrong password!", "Error", JOptionPane.ERROR_MESSAGE);
                } 
            }
        });

        new Thread(()->{

        });
        panel.add(label);
        panel.add(passwordField);
        panel.add(unlockButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}