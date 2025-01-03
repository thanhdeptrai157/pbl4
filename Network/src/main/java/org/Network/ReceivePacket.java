package org.Network;


import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


 
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
            if(bytes[0] == 0){
                int ipAddress = SendData.byteToInt(bytes, 1+ 4);
                int countPart = SendData.byteToInt(bytes, 1 + 4 * 2);
                int size = countPart * SendData.sizeData;
                    lock.lock();
                    try {
                        clientScreen.put(ipAddress, new byte[size]);
                        isImageReady.put(ipAddress, false);
                        countPartImage.put(ipAddress, countPart);
                    } catch (Exception e) {
                    } finally {
                        lock.unlock();
                }
            }
            else {
                int ipAddress = SendData.byteToInt(bytes, 1 + 4);
                int sizeArray = SendData.sizeData;
                int ordinal = SendData.byteToInt(bytes, 1 + 4 * 2);

                lock.lock();
                try {
                    System.arraycopy(bytes, 1 + 4 * 3, clientScreen.get(ipAddress), ordinal * sizeArray, sizeArray);
                    int count = countPartImage.get(ipAddress);
                    countPartImage.put(ipAddress, --count);

                    if(count == 0)
                        isImageReady.put(ipAddress, true);
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
