package org.Network;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

 
public class ReceivePacket {
    
    private DatagramSocket socket;
    private int port;

    public ReceivePacket(int port){
        this.port = port;
        try {
           // socket = new DatagramSocket(this.port);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }   

    public byte[] Receive(){
        byte[] buffer = new byte[1024];
        try {
            
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Dang doi tin nhan");
            buffer = ACK.Receive(port);
            System.out.println("da nhan duoc tin nhan");

            InfoPacket info = Convert(buffer, InfoPacket.class);

            int size = info.getSizeElementPacket();
            buffer = new byte[size * info.getCount()];
            boolean[] map = new boolean[info.getCount()];
            int count = 0;
            while(count != info.getCount()){
                ImageData image = Convert(ACK.Receive(port), ImageData.class);
                if(!map[image.getOrdinal()]){
                    ++count;
                    map[image.getOrdinal()] = true;
                    System.arraycopy(image.getData(), 0, buffer, image.getOrdinal() * size, size);
                }
            }

        } catch (Exception e) {
            //TODO: handle exception
        } 

        System.out.println(buffer);
        return buffer;
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
