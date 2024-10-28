package org.Server;


import org.Network.ReceivePacket;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class MainServer {
    private ServerSocket serverSocket;
    private Map<String, Socket> socketMap = new HashMap<>();
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
    }

    public void startServer(ClientConnectionListener client) throws IOException {
        while(true){
            System.out.println("Server đang chờ kết nối");
            Socket clientSocket = serverSocket.accept();
            socketMap.put(clientSocket.getInetAddress().getHostAddress(), clientSocket);
            System.out.println("Client đã kết nối: " + clientSocket.getInetAddress().getHostName());
            if (client != null) {
                client.onClientConnected(clientSocket.getInetAddress().getHostAddress());
            }
            //new ClientHandler(clientSocket).start();
        }
    }
    public ReceivePacket getReceivePacket(){
        return receivePacket;
    }
    public Map<String, Socket> getSocketMap(){
        return socketMap;
    }
}