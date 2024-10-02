package org.Client;

import org.Network.SendData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        SendData sendData = new SendData("127.0.0.1", 5002, "127.0.0.1");
        byte[] imageInBytes = null;
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(0, 0, 1570, 780);
        while (true) {
            try {
                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenShot, "jpg", baos);
                imageInBytes = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                System.out.println("Error MainClient  : " + e.getMessage());
            }
            sendData.Send(imageInBytes);
        }

    }
}
