package org.module;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.Network.ReceivePacket;

public class MainServer {

    public static JLabel label;
    public static JFrame frame;

    public static void main(String[] args) {
        UI();

        Thread thread = new Thread(() -> {
            ReceivePacket receive = new ReceivePacket(5000);

            while (true) {
                try {
                    byte[] bytes = receive.Receive();
                    BufferedImage image = null;
                    try {
                        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                        image = ImageIO.read(bis);
                    } catch (IOException e) {
                        System.out.println("Lỗi từ MainServer: " + e.getMessage());
                    }

                    // Xóa ảnh cũ và cập nhật ảnh mới
                    if (image != null) {
                        updateImage(image);
                        Thread.sleep(500); // Hiển thị ảnh trong 2 giây
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    public static void UI() {
        // Tạo một JFrame
        frame = new JFrame("Hiển thị ảnh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Thiết lập frame hiển thị ở giữa màn hình
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    public static void updateImage(BufferedImage image) {
        if (label != null) {
            frame.remove(label);
        }

        ImageIcon imageIcon = new ImageIcon(image);
        label = new JLabel(imageIcon);
        frame.add(label);
        frame.revalidate(); // kiểm tra sự thay đổi của các thành phần con và tính toán lại kích thước để vẽ lại
       frame.repaint(); // vẽ lại toàn bộ container và thành phần con => hiển thị ngay lập tức
    }
}
