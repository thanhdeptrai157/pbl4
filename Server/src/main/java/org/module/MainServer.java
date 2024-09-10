package org.module;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.Network.InfoPacket;
import org.Network.ReceivePacket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class MainServer {
       
    public static JLabel label;
    public static JFrame frame;
    public static byte[] imageBytes = null ;
    public static void main(String[] args) throws InterruptedIOException{

        UI();

        Thread thread = new Thread( () -> {

        ReceivePacket receive = new ReceivePacket(5000);
            for(int i = 0; i < 3000; ++i){

                byte[] bytes = receive.Receive();

                BufferedImage image = null;
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    image = ImageIO.read(bis);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                label = new JLabel();
                frame.add(label);
                // Nếu ảnh không null, chuyển thành ImageIcon
                if (image != null) {
                    ImageIcon imageIcon = new ImageIcon(image);
                    frame.remove(label);
                    label = new JLabel(imageIcon);
                    frame.add(label);

                }
            }
        });
        thread.start();
    }

    public static void UI(){
        // Tạo một JFrame
        frame = new JFrame("Hiển thị ảnh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Thiết lập frame hiển thị ở giữa màn hình
        frame.setLocationRelativeTo(null);

        
        frame.setVisible(true);

    }

}
