package org.module;


import java.io.BufferedInputStream;
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

        ReceivePacket receive = new ReceivePacket(5000);
        Gson gson = new Gson();
        String s1 = new String(receive.Receive(), StandardCharsets.UTF_8);  
        String s = "{\"id\":2,\"count\":2,\"sizeElementPacket\":10}";
        if(s1.equals(s))
            System.out.println("Giong nhau");
        else
            System.out.println("Khac nhau");
        System.out.println(s + " size" + s.length() );
        System.out.println(s1 + " size" + s.length() );

        JsonReader reader = new JsonReader(new StringReader(s1));
        reader.setLenient(true); // Enable lenient mode

        try {
            InfoPacket info = gson.fromJson(reader, InfoPacket.class);

            System.out.println(info.toString());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    public static void UI(){
        // Tạo một JFrame
        frame = new JFrame("Hiển thị ảnh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Thiết lập frame hiển thị ở giữa màn hình
        frame.setLocationRelativeTo(null);

        // Hiển thị JFrame
        Thread thread1 = new Thread(() -> {
        });
        thread1.start();
        label = new JLabel();
        frame.add(label);
        frame.setVisible(true);

    }

}
