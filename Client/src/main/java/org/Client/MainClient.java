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
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.Network.ReceiverTransfer;
import org.Network.SendData;

public class MainClient {
    private Thread view;
    private Thread dashboard;
    private final String ipServer;
    private boolean isConnected;
    private boolean isLocking;
    private final Socket cmdSocket;
    private final Socket chatSocket;
    private final Socket fileSocket;
    private final int numberClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private Thread lock;
    private Stage stage;
    private LockScreen lockScreen = new LockScreen();
    public MainClient(String url, int port,Stage stage) {
        ipServer = url;

        this.stage = stage;
        try {
            cmdSocket = new Socket(url, port);
            chatSocket = new Socket(url, 5003);
            fileSocket = new Socket(url, 5004);
            BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
            String s = br.readLine();
            numberClient = Integer.parseInt(s);
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            throw new RuntimeException(e);
        }
        getFile();
        isLocking = false;
    }
    public void setStage(Stage stage){
        this.stage = stage;
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
                                //showToast(stage, "Share màn hình");
                            break;
                        case "viewSmall":
                                if (dashboard == null || !dashboard.isAlive()) {
                                    dashboard = new Thread(() -> {
                                        try {
                                            receiveScreenDashboard();
                                        } catch (AWTException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    dashboard.start();

                                }
                            showToast(stage, "Share màn hình");
                            break;
                        case "notViewSmall":
                            if (dashboard != null && dashboard.isAlive()) {
                                dashboard.interrupt();
                                System.out.println("View thread stopped.");
                            }
                            break;
                        case "lock":
                            isLocking = true;
                            lock = new Thread(()->{
                                if(isLocking){
                                    this.lockScreen();
                                }
                            });
                            lock.start();
                            break;
                        case "notView":
                            if (view != null && view.isAlive()) {
                                view.interrupt();
                                System.out.println("View thread stopped.");
                            }
                            break;
                        case "unlock":
                            isLocking = false;
                            lock.interrupt();
                            lockScreen.unlockScreen();
                            break;
                        case "history":
                            int count = 5;
                            PrintWriter printWriter = new PrintWriter(cmdSocket.getOutputStream(), true);
                            printWriter.println(count);
                            for(History s : HistoryWeb.getHistoryWeb(count)){
                                printWriter.println(s.getDate() +"$"+ s.getUrl());
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
                            } else if (event.equals("Mess:")) {
                                showToast(stage, command);
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

    public void showToast(Stage Stage, String message) {
        Platform.runLater(()->{
            Popup popup = new Popup();
            popup.setAutoHide(true);
            Text text = new Text(message);
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");


            HBox hBox = new HBox(text);
            hBox.setStyle("-fx-background-color: #323232; -fx-padding: 15px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");


            StackPane pane = new StackPane(hBox);
            pane.setStyle("-fx-background-color: transparent;");
            popup.getContent().add(pane);

            popup.show(Stage, Stage.getX() + Stage.getWidth() / 2 - 150, Stage.getY() + 50);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), hBox);
            slideIn.setFromY(-50);
            slideIn.setToY(0);
            slideIn.play();
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), hBox);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> popup.hide());
                fadeOut.play();
            });
            delay.play();
        });
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
                    receiveFile.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public Socket getChatSocket() {
        return chatSocket;
    }

    public void lockScreen()  {
        try{
            lockScreen.lockScreen();
        }
        catch (Exception e){
            e.printStackTrace();
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

    public void receiveScreenDashboard() throws AWTException, InterruptedException {
        System.out.println("View");
        SendData sendData = new SendData(ipServer, 5005, numberClient);
        System.out.println("client " + numberClient);
        byte[] imageInBytes = null;
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rect = new Rectangle(screenSize);

        while (!Thread.currentThread().isInterrupted()) {
            try {

                BufferedImage screenShot = robot.createScreenCapture(rect);

                int newWidth = screenShot.getWidth() / 5;
                int newHeight = screenShot.getHeight() / 5;
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_INDEXED);
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(screenShot, 0, 0, newWidth, newHeight, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(1.0f);
                writer.setOutput(new MemoryCacheImageOutputStream(baos));
                writer.write(null, new IIOImage(resizedImage, null, null), param);
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
