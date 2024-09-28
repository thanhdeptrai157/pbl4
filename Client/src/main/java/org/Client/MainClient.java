package org.Client;

import java.awt.AWTException;
import java.io.IOException;
import java.net.Socket;

public class MainClient {
    private boolean isConnected;
    private Socket socket;
    public MainClient(String url, int port){
        try {
            socket = new Socket(url, port);
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket(){
        return socket;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public static void main(String[] args) throws AWTException, InterruptedException {

    }

}
