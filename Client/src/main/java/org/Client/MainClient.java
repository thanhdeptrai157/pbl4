package org.Client;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.Network.ReceiverTransfer;
import org.Network.SendData;

public class MainClient {
    private Thread view;
    private final String ipServer;
    private boolean isConnected;
    private final Socket cmdSocket;
    private final Socket chatSocket;
    private final Socket fileSocket;
    private final int numberClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public MainClient(String url, int port) {
        ipServer = url;
        try {
            cmdSocket = new Socket(url, port);
            chatSocket = new Socket(url, 5003);
            fileSocket = new Socket(url, 5004);
            //lưu số do Server gửi về để truyền ảnh
            BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
            String s = br.readLine();
            numberClient = Integer.parseInt(s);
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            throw new RuntimeException(e);
        }
        getFile();
    }

    public Socket getSocketCmd() {
        return cmdSocket;
    }
    public Socket getFileSocket(){
        return fileSocket;
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void commandFromServer() throws IOException, InterruptedException, AWTException {
        cmdSocket.setSoTimeout(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while (true) {
            try {
                String command = br.readLine();
                if (command != null) {
                    switch (command.trim()) {
                        case "view":
                            if (view == null || !view.isAlive()) {
                                view = new Thread(() -> {
                                    try {
                                        receiveScreen();
                                    } catch (AWTException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                                view.start();
                            }
                            break;

                        case "lock":
                            executorService.submit(this::lockScreen);
                            break;

                        case "notView":
                            if (view != null && view.isAlive()) {
                                view.interrupt();
                                System.out.println("View thread stopped.");
                            }
                            break;

                        case "exit":
                            System.out.println("Exiting command loop.");
                            if (view != null && view.isAlive()) {
                                view.interrupt();
                            }
                            shutdownExecutor();
                            return;
                        default:
                            String[] commandSplit = command.split(" ");
                            String event = commandSplit[0];
                            if (event.equals("move")) {
                                double x = Double.parseDouble(commandSplit[1]) * screenSize.getWidth() / 1200;
                                double y = Double.parseDouble(commandSplit[2]) * screenSize.getHeight() / 680;
                                robot.mouseMove((int) x, (int) y);
                            } else if (event.equals("click")) {
                                String clickType = commandSplit[3];
                                if (clickType.equals("left")) {
                                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                } else if (clickType.equals("right")) {
                                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                                }
                            } else if (event.equals("type")) {
                                System.out.println(command);
                                int keyCode = Integer.parseInt(commandSplit[1]);
                                boolean isShiftPress = Boolean.parseBoolean(commandSplit[2]);
                                boolean isCtrlPress = Boolean.parseBoolean(commandSplit[3]);
                                boolean isAltPress = Boolean.parseBoolean(commandSplit[4]);
                                if (isShiftPress) robot.keyPress(KeyEvent.VK_SHIFT);
                                if (isCtrlPress) robot.keyPress(KeyEvent.VK_CONTROL);
                                if (isAltPress) robot.keyPress(KeyEvent.VK_ALT);
                                if(keyCode != 0 && keyCode != 16 && keyCode!= 18 && keyCode != 17){
                                    robot.keyPress(keyCode);
                                    robot.keyRelease(keyCode);
                                }
                                if(isShiftPress) robot.keyRelease(KeyEvent.VK_SHIFT);
                                if(isCtrlPress) robot.keyRelease(KeyEvent.VK_CONTROL);
                                if(isAltPress) robot.keyRelease(KeyEvent.VK_ALT);
                                System.out.println(keyCode);
                            }
                            else if(event.equals("scroll")){
                                int delta = Integer.parseInt(commandSplit[1]);
                                robot.mouseWheel(delta);
                            }
                            break;
                    }
                } else {
                    System.out.println("Server disconnected.");
                    break;
                }
            } catch (Exception e) {
                if (!(e instanceof java.net.SocketTimeoutException)) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
    public void getFile(){
        String filePathReceive = "Client/files/";
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                ReceiverTransfer receiveFile = new ReceiverTransfer(filePathReceive, fileSocket);
                receiveFile.start();
                try {
                    receiveFile.join(); // Đợi quá trình nhận file hoàn tất trước khi lắng nghe file tiếp theo
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public Socket getChatSocket() {
        return chatSocket;
    }

    public void lockScreen() {
        String command = "rundll32.exe user32.dll,LockWorkStation";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveScreen() throws AWTException, InterruptedException {
        System.out.println("View");
        SendData sendData = new SendData(ipServer, 5002, numberClient);
        System.out.println("client " + numberClient);
        byte[] imageInBytes = null;
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rect = new Rectangle(screenSize);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.3f);
                writer.setOutput(new MemoryCacheImageOutputStream(baos));
                writer.write(null, new IIOImage(screenShot, null, null), param);
                writer.dispose();
                imageInBytes = baos.toByteArray();
                baos.close();
                Thread.sleep(10);
            } catch (Exception e) {
                System.out.println("Error MainClient  : " + e.getMessage());
                break;
            }
            sendData.Send(imageInBytes);
        }

        System.out.println("receiveScreen stopped.");
    }
}
