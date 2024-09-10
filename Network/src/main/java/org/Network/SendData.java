package org.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.gson.Gson;


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
        System.out.println("Gui packet");
        try {
            
            InfoPacket info = getInfor(buffer); 

            Gson gson = new Gson();
            byte[] bytes = gson.toJson(info).getBytes();
            ACK.Send(bytes, inetAddress, port);

            for(int i = 0; i < info.getCount(); ++i){
                byte[] bytesImage = new byte[info.getSizeElementPacket()];
                int size = Math.min(info.getSizeElementPacket(), buffer.length - (i) * info.getSizeElementPacket());
                System.arraycopy(buffer, i*info.getSizeElementPacket(), bytesImage, 0, size);
                ImageData imageData = new ImageData(i, bytesImage);
                byte[] send = gson.toJson(imageData).getBytes();
                ACK.Send(send, inetAddress, port);

            }

            System.out.println(info.toString());
        } catch (Exception e) {
            System.err.println("Send: " + e);
        }
    }


    
    public static InfoPacket getInfor(byte[] bytes){
        int sizePacket = 4096;

        InfoPacket info = new InfoPacket((short)2, (short)(bytes.length / sizePacket + 1), (short)sizePacket);
        return info;
    }
}
