package org.Server;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import org.Network.*;

public class MainServer {
    private final ServerSocket serverSocket;
    private final ServerSocket serverSocketChat;
    private final ServerSocket serverTransFiles;
    private final Map<String, Socket> socketMap = new HashMap<>();
    private final Map<String, Socket> socketMapChat = new HashMap<>();
    private final Map<String, Socket> socketMapFile= new HashMap<>();
    private static MainServer instance = null;
    private final ReceivePacket receivePacket;
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
        serverTransFiles = new ServerSocket(5004);
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

            //Socket cho file
            Socket clientSocketFile = serverTransFiles.accept();
            socketMapFile.put(clientSocketFile.getInetAddress().getHostAddress(), clientSocketFile);
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
    public Map<String, Socket> getSocketMapFile(){
        return socketMapFile;
    }
    public Map<String, Socket> getSocketMapChat(){
        return socketMapChat;
    }
}