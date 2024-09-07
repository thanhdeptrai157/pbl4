package testJDK;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

public class TCPClient {

	public static void main(String[] args) throws AWTException, InterruptedException {
		ClientUDP();

	}
	
	public static void ClientUDP() {
		try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("192.168.43.129");

            for(int j = 0; j < 100; ++j) {
            	Robot robot = new Robot();
                Rectangle rect = new Rectangle(0, 0, 1200, 600);

                BufferedImage screenShot = robot.createScreenCapture(rect);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenShot, "jpg", baos); // 
              
                byte[] buffer = baos.toByteArray();
              
                int countPacket = buffer.length / 1024 + 1;
                
                byte[] count = (""+buffer.length).getBytes();
                System.out.println(buffer.length);
                DatagramPacket packet = new DatagramPacket(count, count.length, address, 5000);
                
                //ack(socket, packet);
                socket.send(packet);              
                Thread.sleep(100);
                for(int i = 0; i < countPacket; ++i) {
                	int size = Math.min(countPacket * 1024 - (i + 1)* 1024, 1024);
                    packet = new DatagramPacket(buffer,i * 1024, size, address, 5000);
                    //ack(socket, packet);
                    socket.send(packet); 
                    Thread.sleep(100);
                }

                Thread.sleep(10000);
                System.out.println("==================================================================");
            }
            
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void ack(DatagramSocket socket, DatagramPacket packet) throws IOException {
		
        boolean acknowledged = false;
        int attempts = 0;
        int maxAttempts = 10;
        int timeout = 2000; // 2 seconds
        
        while (!acknowledged) {
            socket.send(packet);
            System.out.println("Packet sent. Awaiting acknowledgment...");

            // Set socket timeout
            socket.setSoTimeout(timeout);
            
            try {
                byte[] ackBuffer = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);
                String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength(), StandardCharsets.UTF_8);

                if ("ACK".equals(ackMessage)) {
                    acknowledged = true;
                    System.out.println("Acknowledgment received.");
                }
            } catch (Exception e) {
                attempts++;
                System.out.println("No acknowledgment received. Retrying... (" + attempts + ")");
            }
        }
		
	}
	
 	public static void ClientTCP() throws InterruptedException, AWTException {
        try {
            // Kết nối tới server tại địa chỉ localhost và cổng 5000
            Socket socket = new Socket("192.168.21.130", 5000);
            OutputStream out = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);

         // Tạo luồng đầu ra để gửi dữ liệu tới server
            for(int i = 0; i<1; ++i) {
            
            
	            Robot robot = new Robot();
	            Rectangle rect = new Rectangle(0, 0, 1200, 600);
	
	            BufferedImage screenShot = robot.createScreenCapture(rect);
	            //System.out.println("Size" + screenShot.getHeight());
	            //ImageIO.write(screenShot, "JPG", new File("C:\\Users\\CONG THANH\\OneDrive\\Hình ảnh\\Ảnh chụp màn hình\\a.jpg"));
	            
	            // Đọc file hình ảnh và gửi tới server
	            //FileInputStream fis = new FileInputStream("D:\\2263.jpg_wh860.jpg");
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(screenShot, "jpg", baos); // 
	            byte[] imageBytes = baos.toByteArray();
	            ;

	            // Tạo BufferedInputStream từ ByteArrayInputStream
	            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
	
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = bis.read(buffer)) != -1) {
	                bos.write(buffer, 0, bytesRead);
	            }
	            
	            // Đóng kết nối
	            Thread.sleep(1000);
	            System.out.println("gui tin hieu");
            }
            out.close();
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }		
		
	}
}
