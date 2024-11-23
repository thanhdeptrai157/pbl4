package org.Network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;


public class SendData {
    
    private DatagramSocket socket;
    private InetAddress inetAddress;
    private int port;
    private int intAdd;
    
    public static final int sizeData = 1024 * 20;
    public static final int ipAddress = 4;
    public static final int numberOfPart = 4;
    public static final int ordinal = 4;
    public static final int id = 4;
    public static final int type = 1;



    public SendData(String address, int port, int numClient){
        try {
            intAdd = numClient;
            System.out.println(intAdd);
            socket = new DatagramSocket();
            inetAddress = InetAddress.getByName(address);
            this.port = port;
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public void Send(byte[] buffer){
        
        byte[] bytes = new byte[sizeData + ipAddress + numberOfPart + id + type];
        int count = buffer.length / sizeData + 1;
        int idAck = new Random().nextInt();

        byte[] byteCount = intToByteArray(count);
        byte[] byteIpaddress = intToByteArray(intAdd);
        byte[] byteId = intToByteArray(idAck);

        System.arraycopy(byteCount, 0, bytes, sizeData, byteCount.length);
        System.arraycopy(byteIpaddress, 0, bytes, sizeData + numberOfPart, byteIpaddress.length);
        System.arraycopy(byteId, 0, bytes, sizeData + numberOfPart + ipAddress, byteId.length);
        //System.arraycopy(buffer, 0, bytes, 0, sizeData);
        //System.arraycopy(buffer, 0, bytes, 0, buffer.length);
        bytes[sizeData + ipAddress + numberOfPart + id] = 0;
        ACK.Send(bytes, inetAddress, port);
        for(int i = 0; i < count; ++i){
                idAck = new Random().nextInt();
                int size = Math.min(sizeData, buffer.length - (i) * sizeData);

                byte[] byteOrdinal = intToByteArray(i);
                byteIpaddress = intToByteArray(intAdd);
                byteId = intToByteArray(idAck);

                System.arraycopy(buffer, i * sizeData, bytes, 0, size);
                System.arraycopy(byteOrdinal, 0, bytes, sizeData, byteCount.length);
                System.arraycopy(byteIpaddress, 0, bytes, sizeData + ordinal, byteIpaddress.length);
                System.arraycopy(byteId, 0, bytes, sizeData + ordinal + ipAddress, byteId.length);
                bytes[sizeData + ipAddress + numberOfPart + id] = 1;

                ACK.Send(bytes, inetAddress, port);
            }

    }

    public static byte[] intToByteArray(int number) {
        // Tạo một mảng byte với kích thước 4 (vì int chiếm 4 byte)
        byte[] byteArray = new byte[4];

        // Chuyển đổi từng byte
        byteArray[0] = (byte) (number >> 24); // Byte cao nhất
        byteArray[1] = (byte) (number >> 16); 
        byteArray[2] = (byte) (number >> 8);
        byteArray[3] = (byte) number; // Byte thấp nhất

        return byteArray;
    }

    public static int byteToInt(byte[] bytes, int index){
        int value = 0;
        value |= bytes[index]<<24;
        value |= bytes[index + 1]<<16;
        value |= bytes[index + 2]<<8;
        value |= bytes[index + 3];
        return value;
    }

}
