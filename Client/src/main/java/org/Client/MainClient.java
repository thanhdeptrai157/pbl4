package org.Client;

import org.Network.SendData;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
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
    private String ipClient;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    public MainClient(String url, int port){
        ipServer = url;
        try {
            cmdSocket = new Socket(url, port);
            chatSocket = new Socket(url, 5003);
            //lưu địa chỉ ip do Server gửi về để truyền ảnh
            BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
            String s = br.readLine();
            ipClient = s;
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
            throw new RuntimeException(e);
        }
    }
    public Socket getSocketCmd(){
        return cmdSocket;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void commandFromServer() throws IOException, InterruptedException, AWTException {
        System.out.println("Command listener started.");
        cmdSocket.setSoTimeout(1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));

        while (true) {
            try {
                String s = br.readLine();
                if (s != null) {
                    System.out.println("Received command: " + s.trim());
                    switch (s.trim()) {
                        case "view":
                            if (view == null || !view.isAlive()) { // Khởi tạo luồng mới nếu chưa có hoặc đã dừng
                                view = new Thread(() -> {
                                    try {
                                        receiveScreen();
                                    } catch (AWTException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                                view.start(); // Khởi động luồng view
                            }
                            break;

                        case "lock":
                            executorService.submit(this::lockScreen); // Khởi chạy lockScreen trong ExecutorService
                            break;

                        case "notView":
                            if (view != null && view.isAlive()) {
                                view.interrupt(); // Dừng luồng view nếu đang chạy
                                System.out.println("View thread stopped.");
                            }
                            break;

                        case "exit":
                            System.out.println("Exiting command loop.");
                            if (view != null && view.isAlive()) {
                                view.interrupt(); // Dừng view nếu còn đang chạy
                            }
                            shutdownExecutor(); // Tắt ExecutorService
                            return; // Thoát khỏi vòng lặp

                        default:
                            System.out.println("Unknown command: " + s.trim());
                            break;
                    }
                } else {
                    System.out.println("Server disconnected.");
                    break;
                }
            } catch (IOException e) {
                // Xử lý ngoại lệ khi không nhận được dữ liệu, có thể do timeout
                if (!(e instanceof java.net.SocketTimeoutException)) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void shutdownExecutor() {
        executorService.shutdown(); // Đóng ExecutorService một cách an toàn
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Dừng ngay lập tức nếu sau 5 giây vẫn chưa tắt xong
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public Socket getChatSocket(){
        return chatSocket;
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
        SendData sendData = new SendData(ipServer, 5002, "172.1.1.1");
        System.out.println("client " + ipClient);
        byte[] imageInBytes = null;
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rect = new Rectangle(screenSize);
        while (!Thread.currentThread().isInterrupted()) { // Kiểm tra trạng thái interrupt
            try {
                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.7f); // Đặt chất lượng ảnh nén (giảm dung lượng)

                // Ghi ảnh nén vào ByteArrayOutputStream
                writer.setOutput(new MemoryCacheImageOutputStream(baos));
                writer.write(null, new IIOImage(screenShot, null, null), param);
                writer.dispose();

                imageInBytes = baos.toByteArray();
                baos.close();
                //System.out.println(imageInBytes.length);
            } catch (Exception e) {
                System.out.println("Error MainClient  : " + e.getMessage());
                break; // Thoát khỏi vòng lặp nếu có lỗi
            }
            sendData.Send(imageInBytes);
        }

        System.out.println("receiveScreen stopped."); // Xác nhận đã dừng
    }
}
