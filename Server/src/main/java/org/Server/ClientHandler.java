package org.Server;

import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clienSocket;

    public ClientHandler(Socket socket){
        clienSocket = socket;
    }
    @Override
    public void run(){

    }

}
