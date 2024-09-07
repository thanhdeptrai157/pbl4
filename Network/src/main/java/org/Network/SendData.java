package org.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendData {
    

    private DatagramSocket socket;
    private InetAddress inetAddress;
    private int port;
    public SendData(String address, int port){
        try {
            socket = new DatagramSocket();
            inetAddress = InetAddress.getByName(address);
            this.port = port;
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    
    public void Send(byte[] buffer){
        try {
            DatagramPacket Packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            socket.send(Packet);
            System.out.println("Gui packet");
        } catch (Exception e) {
            //TODO: handle exception
        }
    }


    public static void ack(DatagramSocket socket, DatagramPacket packet) throws IOException {

        boolean acknowledged = false;
        int attempts = 0;
        int maxAttempts = 10;
        int timeout = 2000; // 2 seconds
        
        while (!acknowledged) {
            socket.send(packet);
            System.out.println("Packet sent. Awaiting acknowledgment...");

            // Set socket timeout
            socket.setSoTimeout(timeout);
            
            try {
                byte[] ackBuffer = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);
                String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength(), java.nio.charset.StandardCharsets.UTF_8);

                if ("ACK".equals(ackMessage)) {
                    acknowledged = true;
                    System.out.println("Acknowledgment received.");
                }
            } catch (Exception e) {
                attempts++;
                System.out.println("No acknowledgment received. Retrying... (" + attempts + ")");
            }
        }
		
	}
}
