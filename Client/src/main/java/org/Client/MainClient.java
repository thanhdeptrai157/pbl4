package org.Client;

import org.Network.SendData;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MainClient {
    private Thread view;
    private String ipServer;
    private boolean isConnected;
    private Socket cmdSocket;
    private Socket chatSocket;
    private int numberClient;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MainClient(String url, int port) {
        ipServer = url;
        try {
            cmdSocket = new Socket(url, port);
            chatSocket = new Socket(url, 5003);
            //lưu số do Server gửi về để truyền ảnh
            BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
            String s = br.readLine();
            numberClient = Integer.parseInt(s);
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            throw new RuntimeException(e);
        }
    }

    public Socket getSocketCmd() {
        return cmdSocket;
    }

    public boolean isConnected() {
        return isConnected;
    }
    public void commandFromServer() throws IOException, InterruptedException, AWTException {
        System.out.println("Command listener started.");
        cmdSocket.setSoTimeout(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while (true) {
            try {
                String s = br.readLine();
                if (s != null) {
                    switch (s.trim()) {
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

                            String[] s1 = s.split(" ");
                            String event = s1[0];
                            if (event.equals("move")) {
                                double x = Double.parseDouble(s1[1]) * screenSize.getWidth() / 1200;
                                double y = Double.parseDouble(s1[2]) * screenSize.getHeight() / 680;
                                robot.mouseMove((int) x, (int) y);
                            } else if (event.equals("click")) {
                                String clickType = s1[3];
                                if (clickType.equals("left")) {
                                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                } else if (clickType.equals("right")) {
                                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                                }
                            } else if (event.equals("type")) {

                                String character = s1[1];
                                StringSelection stringSelection = new StringSelection(character);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);
                                robot.keyPress(KeyEvent.VK_CONTROL);
                                robot.keyPress(KeyEvent.VK_V);
                                robot.keyRelease(KeyEvent.VK_V);
                                robot.keyRelease(KeyEvent.VK_CONTROL);
                            }
                            break;
                    }
                } else {
                    System.out.println("Server disconnected.");
                    break;
                }
            } catch (IOException e) {
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
                param.setCompressionQuality(0.5f);
                writer.setOutput(new MemoryCacheImageOutputStream(baos));
                writer.write(null, new IIOImage(screenShot, null, null), param);
                writer.dispose();

                imageInBytes = baos.toByteArray();
                baos.close();

            } catch (Exception e) {
                System.out.println("Error MainClient  : " + e.getMessage());
                break;
            }
            sendData.Send(imageInBytes);
        }

        System.out.println("receiveScreen stopped.");
    }
}
