package UI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.Server.ClientConnectionListener;
import org.Server.MainServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class ServerController implements ClientConnectionListener {

    @FXML
    private AnchorPane mainLayout;
    private ChatUI chatUI;
    @FXML
    public void initialize() throws IOException {
        chatUI = new ChatUI();
        new Thread(() -> {
            try {
                MainServer.getInstance().startServer(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

//    public void showImage() throws InterruptedException {
//        while (true) {
//            try {
//                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive("127.0.0.1");
//                BufferedImage bufferedImage = null;
//                if (imageBytes != null) {
//                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
//                    try {
//                        bufferedImage = ImageIO.read(bis);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (bufferedImage != null) {
//                        BufferedImage finalBufferedImage = bufferedImage;
//                        Platform.runLater(() -> {
//                            try {
//                                Image fxImage = SwingFXUtils.toFXImage(finalBufferedImage, null);
//                                imageView.setImage(fxImage);
//                                imageView.toFront();
//                            } catch (Exception e) {
//                                System.out.println("Error displaying image: " + e.getMessage());
//                            }
//                        });
//                    } else {
//                        System.out.println("BufferedImage is null, possibly corrupted image.");
//                    }
//                } else {
//                    System.out.println("Received null image data.");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//        }
//    }
    private void openClientScreen() throws InterruptedException {
        Platform.runLater(() -> {
            Stage clientStage = new Stage();
            clientStage.setTitle("Client Screen");

            ImageView clientImageView = new ImageView();
            clientImageView.setFitWidth(1200);
            clientImageView.setFitHeight(700);
            clientImageView.setPreserveRatio(true);

            AnchorPane layout = new AnchorPane(clientImageView);
            AnchorPane.setTopAnchor(clientImageView, 0.0);
            AnchorPane.setLeftAnchor(clientImageView, 0.0);
            AnchorPane.setRightAnchor(clientImageView, 0.0);
            AnchorPane.setBottomAnchor(clientImageView, 0.0);

            Scene scene = new Scene(layout, 1200, 700);
            clientStage.setScene(scene);
            clientStage.show();
            new Thread(() -> {
                try {
                    showImageInView(clientImageView);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
    private void showImageInView(ImageView clientImageView) throws InterruptedException {
        while (true) {
            try {
                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive("127.0.0.1");
                BufferedImage bufferedImage = null;
                if (imageBytes != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    try {
                        bufferedImage = ImageIO.read(bis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bufferedImage != null) {
                        BufferedImage finalBufferedImage = bufferedImage;
                        Platform.runLater(() -> {
                            try {
                                Image fxImage = SwingFXUtils.toFXImage(finalBufferedImage, null);
                                clientImageView.setImage(fxImage);
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
    private void addClientIndicator(String clientIP) {

        Rectangle clientIndicator = new Rectangle(400, 300);
        clientIndicator.setFill(Color.LIGHTBLUE);
        clientIndicator.setStroke(Color.DARKBLUE);
        clientIndicator.setArcHeight(10);
        clientIndicator.setArcWidth(10);


        Label clientLabel = new Label("Client IP: " + clientIP);
        clientLabel.setTextFill(Color.BLACK);
        clientLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");


        Button viewScreenButton = new Button("Xem màn hình");
        viewScreenButton.setStyle("-fx-font-size: 16;");
        viewScreenButton.setOnAction(event -> {
            System.out.println("Button clicked for client: " + clientIP);
            try {
                PrintWriter writer = new PrintWriter(MainServer.getInstance().getClientSocket().getOutputStream(), true);
                writer.println("view");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            new Thread(()->{
                try {
                    openClientScreen();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        Button lockScreenButton = new Button("Lock màn hình");
        lockScreenButton.setStyle("-fx-font-size: 16;");
        lockScreenButton.setOnAction(event -> {
            try {
                PrintWriter writer = new PrintWriter(MainServer.getInstance().getClientSocket().getOutputStream(), true);
                writer.println("lock");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        Button message = new Button("Chat");
        message.setStyle("-fx-font-size: 16;");
        message.setOnAction(event -> {
           Platform.runLater(()->{
               chatUI.launchChatUI("Server");
               try {
                   chatUI.setSocket(MainServer.getInstance().getClientSocket());
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           });

        });
        double yOffset = 50 + (mainLayout.getChildren().size() / 3 * 60);


        AnchorPane.setTopAnchor(clientIndicator, yOffset);
        AnchorPane.setLeftAnchor(clientIndicator, 50.0);


        AnchorPane.setTopAnchor(clientLabel, yOffset + 20);
        AnchorPane.setLeftAnchor(clientLabel, 60.0);

        AnchorPane.setTopAnchor(viewScreenButton, yOffset + 60);
        AnchorPane.setLeftAnchor(viewScreenButton, 60.0);
        AnchorPane.setTopAnchor(lockScreenButton, yOffset + 60);
        AnchorPane.setLeftAnchor(lockScreenButton, 240.0);
        AnchorPane.setTopAnchor(message, yOffset + 120);
        AnchorPane.setLeftAnchor(message, 60.0);
        mainLayout.getChildren().addAll(clientIndicator, clientLabel, viewScreenButton, lockScreenButton, message);
        System.out.println("Client IP: " + clientIP);
    }


    @Override
    public void onClientConnected(String clientIP) {
        Platform.runLater(() -> addClientIndicator(clientIP));
    }
}
