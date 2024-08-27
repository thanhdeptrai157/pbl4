package com.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.*;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App 
{
       
    public static JLabel label;
    public static JFrame frame;
    public static byte[] imageBytes = null ;
    public static void main(String[] args) throws InterruptedIOException{

        UI();
            
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
               ServerUDP();
        });
        thread1.start();
        label = new JLabel();
        frame.add(label);
        frame.setVisible(true);

    }

    public static void Server() throws InterruptedIOException{

            try {
                // Tạo ServerSocket lắng nghe tại cổng 5000
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("Server đang lắng nghe tại cổng 5000...");

                // Chờ client kết nối
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client đã kết nối!");

                // Tạo luồng đầu vào để nhận dữ liệu từ client
                InputStream in = clientSocket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(in);

                int count = 0;
                //while(count++ < 3)
                //{

                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    // Đọc dữ liệu từ client và ghi vào file
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Nhan du lieu thanh cong ");
                    // Chuyển dữ liệu nhận được thành ImageIcon
                    
                    //if(baos.size() != 0);
                        imageBytes = baos.toByteArray();
                    

                    ImageIcon imageIcon = new ImageIcon(imageBytes);

                    label.setIcon(imageIcon);
                    try { 
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        //TODO: handle exception
                    }        
                    System.out.println("Nhan tin hieu");

                //}

                clientSocket.close();
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Loi: " + e);
            }
    }

    public static void ServerUDP(){
        try {
            DatagramSocket socket = new DatagramSocket(5000);
            for(int j = 0; j < 100; ++j){

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);
                System.out.println("Nhan tin hieu");
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + receivedMessage);

                int countBuffer = Integer.parseInt(receivedMessage);
                int count = countBuffer / 1024 + 1;

                byte[] bufferImage = new byte[count * 1024]; 
                for(int i = 0; i < count; ++i){
                    DatagramPacket packets = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packets);
                    String received = new String(packets.getData(), 0, packets.getLength());
                    //System.out.print(received);   
                    System.arraycopy(buffer, 0, bufferImage, i * 1024, buffer.length);

/*
                    String ackMessage = "ACK";
                    byte[] ackBuffer = ackMessage.getBytes(StandardCharsets.UTF_8);
                    InetAddress senderAddress = packet.getAddress();
                    int senderPort = packet.getPort();
                    DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length, senderAddress, senderPort);
                    socket.send(ackPacket);
                    Thread.sleep(500);*/
                }

                ImageIcon imageIcon = new ImageIcon(bufferImage);

                label.setIcon(imageIcon);
                try { 
                    Thread.sleep(1000);
                } catch (Exception e) {
                }        
                System.out.println("Nhan tin hieu");
            }
            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
