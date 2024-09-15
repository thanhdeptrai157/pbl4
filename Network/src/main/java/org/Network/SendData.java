package org.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.crypto.Data;

import com.google.gson.Gson;


public class SendData {
    

    private DatagramSocket socket;
    private InetAddress inetAddress;
    private int port;
    private int intAdd;

    public SendData(String address, int port){
        try {
            intAdd = AddressToInt(InetAddress.getLocalHost().getHostName());
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
            ACK.Send(bytes, InfoPacket.class, inetAddress, port);

            for(int i = 0; i < info.getCount(); ++i){
                byte[] bytesImage = new byte[info.getSizeElementPacket()];
                int size = Math.min(info.getSizeElementPacket(), buffer.length - (i) * info.getSizeElementPacket());
                System.arraycopy(buffer, i*info.getSizeElementPacket(), bytesImage, 0, size);
                DataOrder dataOrder = new DataOrder(intAdd, i, bytesImage);
                byte[] send = gson.toJson(dataOrder).getBytes();
                ACK.Send(send, DataOrder.class, inetAddress, port);

            }

            System.out.println(info.toString());
        } catch (Exception e) {
            System.err.println("Send: " + e);
        }
    }


    
    public InfoPacket getInfor(byte[] bytes){
        int sizePacket = 4096;

        InfoPacket info = new InfoPacket(intAdd, (short)2, (short)(bytes.length / sizePacket + 1), (short)sizePacket);
        return info;
    }

    
    public static int AddressToInt(String ipAddress){
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            byte[] bytes = inet.getAddress();
            
            int result = 0;
            for (byte b : bytes) {
                result = (result << 8) | (b & 0xFF); // Chuyển byte sang số nguyên và dồn vào kết quả
            }

            System.out.println("Địa chỉ IP '" + ipAddress + "' được chuyển thành số nguyên: " + result);
            return result;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
