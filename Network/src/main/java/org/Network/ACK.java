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
    /* 
    public static void Send(byte[] bytes, String classT, InetAddress inetAddress, int port){
        int id = new Random().nextInt();
        
        ACKData ackData = null;
        try {
            ackData = new ACKData(id, classT, bytes);
        } catch (Exception e) {
            System.out.println("Loi khi chuyen ACKData: " + e);
        }
        String s =  new Gson().toJson(ackData).toString();
        String s1 = new String(bytes);
        System.out.println(s);
        System.out.println("================================================================================");
        System.out.println(s1);
        //byte[] ackBytes = s.getBytes();
        byte[] ackBytes = BSONNetwork.<ACKData>ObjectToByte(bytes, ACKData.class);
        
        System.out.println("size ACKData: "+ ackBytes.length);
        boolean acknowledged = false;
        int attempts = 0;
        int maxAttempts = 5;
        int timeout = 33; 

        try {

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket ackPacketSend = new DatagramPacket(ackBytes, ackBytes.length, inetAddress, port); 
            while (!acknowledged && attempts < maxAttempts) {
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
            socket.close();
        } catch (Exception e) {
            System.err.println("ACK send packet: " + e);
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
                //ackData = new Gson().fromJson(reader, ACKData.class);
                ackData = BSONNetwork.<ACKData>ByteToObject(ackBuffer, ACKData.class);
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

    */
    public static void Send(byte[] bytes, InetAddress inetAddress, int port){
        int id = SendData.byteToInt(bytes, SendData.sizeData + 4*2);
        
        try {
            boolean acknowledged = false;
            int attempts = 0;
            int maxAttempts = 5;
            int timeout = 33; 

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket ackPacketSend = new DatagramPacket(bytes, bytes.length, inetAddress, port); 
            while (!acknowledged) {
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
            socket.close();
        } catch (Exception e) {
            System.err.println("ACK send packet: " + e);
        }
    }
    public static byte[] Receive(int port){
        byte[] ackBuffer = new byte[SendData.sizeData + 4 * 3 + 1];
        try {
            DatagramSocket socket = new DatagramSocket(port); 

            DatagramPacket ackPacketReceive = new DatagramPacket(ackBuffer, ackBuffer.length);
            socket.receive(ackPacketReceive);
            
            int id = SendData.byteToInt(ackBuffer, SendData.sizeData + 4*2);
            
            byte[] bytesSend = Integer.toString(id).getBytes();
            DatagramPacket ackPacketSend = new DatagramPacket(bytesSend, bytesSend.length, ackPacketReceive.getAddress(), ackPacketReceive.getPort());
            socket.send(ackPacketSend);
            socket.close();
        } catch (Exception e) { 
            System.err.println("Loi khi nhan ack "+e); 
        } 
        return ackBuffer; 
    } 
}

