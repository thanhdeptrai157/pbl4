package org.Server;


import java.io.IOException;
import java.net.*;


public class MainServer {
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private static MainServer instance = null;
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
    }

    public boolean waitForClient () throws IOException {
        while (true) {
            clientSocket = serverSocket.accept();
            System.out.println("Client đã kết nối: " + clientSocket.getInetAddress());
            return true;
        }
    }
    public Socket getClientSocket(){
        return clientSocket;
    }

}

