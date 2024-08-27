package com.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPRecieverWithAck {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(5000);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);
            String receivedMessage = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            System.out.println("Received message: " + receivedMessage);

            String ackMessage = "ACK";
            byte[] ackBuffer = ackMessage.getBytes(StandardCharsets.UTF_8);
            InetAddress senderAddress = packet.getAddress();
            int senderPort = packet.getPort();
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length, senderAddress, senderPort);
            socket.send(ackPacket);
            System.out.println("Acknowledgment sent.");
        }
    }
}

