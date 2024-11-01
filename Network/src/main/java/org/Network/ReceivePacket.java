package org.Network;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

 
public class ReceivePacket {
    
    private DatagramSocket socket;
    private int port;
    private HashMap<Integer, byte[]> clientScreen;
    private HashMap<Integer, Boolean> isImageReady;
    private HashMap<Integer, Integer> countPartImage;

    private Lock lock;
    public ReceivePacket(int port){
        this.port = port;
        lock = new ReentrantLock();
        clientScreen = new HashMap<Integer, byte[]>();
        isImageReady = new HashMap<Integer, Boolean>();
        countPartImage = new HashMap<Integer, Integer>();
        Thread thread = new Thread(() -> {
            while(true){
                ThreadReceive();
            }
        });

        thread.start();
    }

    public void ThreadReceive(){
        byte[] bytes = ACK.Receive(port);
        try {
            if(bytes[SendData.sizeData + 4*3] == 0){
                int id = SendData.byteToInt(bytes, SendData.sizeData + 4);
                int countPart = SendData.byteToInt(bytes, SendData.sizeData);
                int size = countPart * SendData.sizeData;
                    lock.lock();
                    try {
                        clientScreen.put(id, new byte[size]);
                        isImageReady.put(id, false);
                        countPartImage.put(id, countPart);
                    } catch (Exception e) {
                    } finally {
                        lock.unlock();
                }
            }
            else {
                int id = SendData.byteToInt(bytes, SendData.sizeData + 4);
                int sizeArray = SendData.sizeData;
                int ordinal = SendData.byteToInt(bytes, SendData.sizeData);

                lock.lock();
                try {
                    System.arraycopy(bytes, 0, clientScreen.get(id), ordinal * sizeArray, sizeArray);
                    int count = countPartImage.get(id);
                    countPartImage.put(id, --count);

                    if(count == 0)
                        isImageReady.put(id, true);
                } catch (Exception e) {
                    //TODO: handle exception
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            System.out.println("receive: " + e);
        }
    }
    public byte[] receive(int ipaddress){
        if(clientScreen.containsKey(ipaddress) && isImageReady.containsKey(ipaddress) && isImageReady.get(ipaddress))
        {
            byte[] data = null;
            lock.lock();
            try {
                data = clientScreen.get(ipaddress);
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
            return data;
        }
        return null;
    }
}
