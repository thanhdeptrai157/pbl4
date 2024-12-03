package org.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LockScreen {
    private final String PASSWORD = "12345";
    private JFrame frame;
    private Process process = null;

    public void lockScreen() throws IOException {
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
        JLabel label = new JLabel("MÁY ĐÃ BỊ KHOÁ");
        process = Runtime.getRuntime().exec("Client/blockkey.exe");


        panel.add(label);

        frame.add(panel);
        frame.setVisible(true);
    }
    public void unlockScreen() throws IOException {
        frame.dispose();
        process.destroy();
    }
}