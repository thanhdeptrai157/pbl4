package org.Network;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

 
public class ReceivePacket {
    
    private DatagramSocket socket;
    private int port;

    private HashMap<Integer, LinkedList<byte[]>> clients;
    private HashMap<Integer, byte[]> map;
    private HashMap<Integer, Integer> count;
        
    public ReceivePacket(int port){
        this.port = port;
        
        clients = new HashMap<Integer, LinkedList<byte[]>>();
        map = new HashMap<Integer, byte[]>();
        count = new HashMap<Integer, Integer>();
    }   

    public void ThreadReceive(){
        Thread thread = new Thread(() -> {
            ACKData ackData = ACK.Receive(port);
            if(ackData.getClassT() == InfoPacket.class){
                InfoPacket info = Convert(ackData.getData(), InfoPacket.class);
                System.out.println("Nhan Infopacket");
                count.put(info.getIpAddress(), (int)info.getCount()) ;
                map.put(info.getIpAddress(), new byte[info.getCount() * info.getSizeElementPacket()]);
            }
            else {
                DataOrder dataOrder = Convert(ackData.getData(), DataOrder.class);
                int id = dataOrder.getIpAddress(); 
                int sizeArray = dataOrder.getData().length;
                System.out.println("Nhan DataOrder");
                System.arraycopy(dataOrder.getData(), 0, map.get(id), dataOrder.getOrdinal() * sizeArray, sizeArray);
                count.put(id, count.get(dataOrder.getIpAddress()) - 1);
                if(count.get(id) == 0){
                    if(clients.containsKey(id)){
                        clients.put(id, new LinkedList<byte[]>());
                    }
                    clients.get(id).push(map.get(dataOrder.getIpAddress()));
                    count.remove(id);                }
            }
        }); 
        thread.start();
    }

    public byte[] Receive(int IPaddress){       
        return clients.get(IPaddress).pop();
    }


    public <T> T Convert(byte[] bytes, Class<T> classT){
        String s = new String(bytes, 0, bytes.length); 
        JsonReader reader = new JsonReader(new StringReader(s));
        reader.setLenient(true); // Enable lenient mode

        T t = null;
        try {
            t = new Gson().fromJson(reader, classT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        
        return t;
    }
}
