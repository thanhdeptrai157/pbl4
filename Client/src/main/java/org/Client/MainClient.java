package org.Client;

import org.Network.SendData;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MainClient {
    private Thread view;
    private String ipServer;
    private boolean isConnected;
    private Socket socket;
    public MainClient(String url, int port){
        ipServer = url;
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

    public void commandFromServer() throws IOException, InterruptedException, AWTException {
        System.out.println("command");

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String s = br.readLine();
            if (s != null) {
                System.out.println("Received command: " + s.trim());

                switch (s.trim()) {
                    case "view":
                        if (view == null || !view.isAlive()) { // Start a new thread only if not already running
                            view = new Thread(() -> {
                                try {
                                    receiveScreen();
                                } catch (AWTException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                            view.start(); // Start the view thread
                        }
                        break;

                    case "lock":
                        new Thread(this::lockScreen).start();
                        break;

                    case "notView":
                        if (view != null && view.isAlive()) {
                            view.interrupt();
                        }
                        break;

                    case "exit":
                        System.out.println("Exiting command loop.");
                        if (view != null && view.isAlive()) {
                            view.interrupt();
                        }
                        return;

                    default:
                        System.out.println("Unknown command: " + s.trim());
                        break;
                }
            } else {
                System.out.println("Server disconnected.");
                break;
            }
        }
    }


    public void lockScreen(){
        String command = "rundll32.exe user32.dll,LockWorkStation";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void receiveScreen() throws AWTException, InterruptedException {
        System.out.println("View");
        SendData sendData = new SendData(ipServer, 5002, "127.0.0.1");
        byte[] imageInBytes = null;
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(0, 0, 1580, 780);
        while (true) {
            try {
                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenShot, "jpg", baos);
                imageInBytes = baos.toByteArray();
                baos.close();
                System.out.println(imageInBytes.length);
            } catch (Exception e) {
                System.out.println("Error MainClient  : " + e.getMessage());
            }
            sendData.Send(imageInBytes);
        }

    }
}
