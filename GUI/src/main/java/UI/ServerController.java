package UI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Server.MainServer;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
//import org.module.MainServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerController {
    @FXML
    private ImageView imageView;
    @FXML
    public void initialize() throws IOException {
        ChatUI chatUI = new ChatUI();
        new Thread(()->{
            try {
                MainServer.getInstance().startServer();
                /*if(MainServer.getInstance().waitForClient()){
                    String clientIP = MainServer.getInstance().getClientSocket().getInetAddress().getHostAddress();

                    Platform.runLater(() -> showClientConnectedDialog(clientIP));
                    Platform.runLater(()->{
                        try {
                            chatUI.setSocket(MainServer.getInstance().getClientSocket());
                            chatUI.launchChatUI("Server");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }*/
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        if(MainServer.getInstance().getClientSocket()!= null) {
            Platform.runLater(() -> {
                try {
                    chatUI.setSocket(MainServer.getInstance().getClientSocket());
                    chatUI.launchChatUI("Server");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        new Thread(()->{
            showImage();
        }).start();
       
    }
    @FXML
    public void onClick() throws IOException {
        imageView.setImage(new Image("C:\\Users\\CONG THANH\\Downloads\\Splash art Crying banana cat Final.jpg"));
        imageView.toFront();
    }
    public void showImage() {
        while (true) {
            try {
                // Nhận byte ảnh từ server
                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive("127.0.0.1");
                if (imageBytes != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    // Sử dụng BufferedImage để đọc ảnh từ byte[]
                    BufferedImage bufferedImage = ImageIO.read(bis);

                    if (bufferedImage != null) {
                        Platform.runLater(() -> {
                            try {
                                // Chuyển đổi BufferedImage thành Image của JavaFX
                                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                                // Hiển thị ảnh trên ImageView
                                imageView.setImage(fxImage);
                                imageView.toFront();
                            } catch (Exception e) {
                                System.out.println("Error displaying image: " + e.getMessage());
                            }
                        });
                    } else {
                        System.out.println("BufferedImage is null, possibly corrupted image.");
                    }
                } else {
                    System.out.println("Received null image data.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    private void showClientConnectedDialog(String clientIP) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Connect from Client");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setContentText("Want to connect from client: " + clientIP);
        dialog.show();
    }

}
