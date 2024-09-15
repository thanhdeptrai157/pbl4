package org.Network;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class ACK {
    
    public static void Send(byte[] bytes, Class<?> classT, InetAddress inetAddress, int port){
        int id = new Random().nextInt();

        ACKData ackData = new ACKData(id, classT, bytes);
        byte[] ackBytes = new Gson().toJson(ackData).getBytes();

        boolean acknowledged = false;
        int attempts = 0;
        int maxAttempts = 10;
        int timeout = 2000; // 2 seconds


        try {

            while (!acknowledged) {
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket ackPacketSend = new DatagramPacket(ackBytes, ackBytes.length, inetAddress, port); 
                socket.send(ackPacketSend);

                // Set socket timeout
                socket.setSoTimeout(timeout);

                try {
                    byte[] ackBuffer = new byte[1024];
                    DatagramPacket ackPacketReceive = new DatagramPacket(ackBuffer, ackBuffer.length);
                    socket.receive(ackPacketReceive);
                    String ackMessage = new String(ackPacketReceive.getData(), 0, ackPacketReceive.getLength(), java.nio.charset.StandardCharsets.UTF_8);
                    if (id == Integer.parseInt(ackMessage)) {
                        acknowledged = true;
                    }
                } catch (Exception e) {
                    attempts++;
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public static ACKData Receive(int port){
        ACKData ackData = null;
        try {
            DatagramSocket socket = new DatagramSocket(port); 

            byte[] ackBuffer = new byte[80028];
            DatagramPacket ackPacketReceive = new DatagramPacket(ackBuffer, ackBuffer.length);
            socket.receive(ackPacketReceive);
            
            String s = new String(ackPacketReceive.getData(), 0, ackPacketReceive.getLength());
            JsonReader reader = new JsonReader(new StringReader(s));
            reader.setLenient(true); // Enable lenient mode

            int id = -1;

            try {
                ackData = new Gson().fromJson(reader, ACKData.class);
                id = ackData.getId();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            
            byte[] bytesSend = Integer.toString(id).getBytes();
            DatagramPacket ackPacketSend = new DatagramPacket(bytesSend, bytesSend.length, ackPacketReceive.getAddress(), ackPacketReceive.getPort());
            socket.send(ackPacketSend);
            socket.close();
        } catch (Exception e) { 
            System.err.println("Loi khi nhan ack "+e); 
        } 
        if(ackData != null) return ackData; 
        return null; 
    } 
}
