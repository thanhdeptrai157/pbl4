package org.Network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceivePacket {
    
    private DatagramSocket socket;
    private int port;

    public ReceivePacket(int port){
        this.port = port;
        try {
            socket = new DatagramSocket(this.port);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }   

    public byte[] Receive(){
        byte[] buffer = new byte[1024];
        try {
            
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Dang doi tin nhan");
            socket.receive(packet);
            System.out.println("da nhan duoc tin nhan");
        } catch (Exception e) {
            //TODO: handle exception
        } 

        return buffer;
    }
}
