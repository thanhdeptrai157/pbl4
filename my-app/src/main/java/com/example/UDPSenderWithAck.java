package com.example;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSenderWithAck {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress receiverAddress = InetAddress.getByName("localhost");
        byte[] buffer = "Hello, UDP!".getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 5000);
        
        boolean acknowledged = false;
        int attempts = 0;
        int maxAttempts = 5;
        int timeout = 2000; // 2 seconds

        while (!acknowledged && attempts < maxAttempts) {
            socket.send(packet);
            System.out.println("Packet sent. Awaiting acknowledgment...");

            // Set socket timeout
            socket.setSoTimeout(timeout);
            
            try {
                byte[] ackBuffer = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);
                String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength(), StandardCharsets.UTF_8);

                if ("ACK".equals(ackMessage)) {
                    acknowledged = true;
                    System.out.println("Acknowledgment received.");
                }
            } catch (Exception e) {
                attempts++;
                System.out.println("No acknowledgment received. Retrying... (" + attempts + ")");
            }
        }

        if (!acknowledged) {
            System.out.println("Failed to receive acknowledgment after " + maxAttempts + " attempts.");
        }

        socket.close();
    }
}

