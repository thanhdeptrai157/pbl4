package org.Server;


import org.Network.ReceivePacket;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class MainServer {
    private ServerSocket serverSocket;
    private ServerSocket serverSocketChat;
    private Map<String, Socket> socketMap = new HashMap<>();
    private Map<String, Socket> socketMapChat = new HashMap<>();
    private static MainServer instance = null;
    private ReceivePacket receivePacket;
    public static MainServer getInstance() throws IOException {
        if(instance == null){
            synchronized (MainServer.class){
                if(instance == null){
                    instance = new MainServer();
                }
            }
        }
        return instance;
    }

    private MainServer() throws IOException {
        serverSocket = new ServerSocket(5001);
        receivePacket = new ReceivePacket(5002);
        serverSocketChat = new ServerSocket(5003);
    }

    public void startServer(ClientConnectionListener client) throws IOException {
        int count = 0;
        while(true){
            System.out.println("Server đang chờ kết nối");
            //Socket cho cmd
            Socket clientSocket = serverSocket.accept();
            count++;
            socketMap.put(clientSocket.getInetAddress().getHostAddress(), clientSocket);

            //Socket cho chat
            Socket clientSocketChat = serverSocketChat.accept();
            socketMapChat.put(clientSocketChat.getInetAddress().getHostAddress(), clientSocketChat);
            //Truyen dia chi ve cho client nhan
            PrintWriter wr = new PrintWriter(clientSocket.getOutputStream(), true);
            wr.println(count);

            System.out.println("Client đã kết nối: " + clientSocket.getInetAddress().getHostName());
            if (client != null) {
                client.onClientConnected(clientSocket.getInetAddress().getHostAddress());
            }

        }
    }
    public ReceivePacket getReceivePacket(){
        return receivePacket;
    }
    public Map<String, Socket> getSocketMap(){
        return socketMap;
    }
    public Map<String, Socket> getSocketMapChat(){
        return socketMapChat;
    }
}