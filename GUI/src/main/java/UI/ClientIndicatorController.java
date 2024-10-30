package UI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.Server.MainServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientIndicatorController {
    private volatile boolean isRunning = true;
    private ChatUI chatUI;
    public void stopImageReceiving() {
        isRunning = false;
    }
    @FXML
    private Rectangle clientIndicator;
    @FXML
    private Label clientLabel;
    @FXML
    private Button viewScreenButton;
    @FXML
    private Button lockScreenButton;
    @FXML
    private Button messageButton;
    @FXML
    private ImageView computerIcon;
    private PrintWriter writer;
    public void initialize(String clientIP, ChatUI chatUI) throws IOException {
        clientLabel.setText("Client IP: " + clientIP);
        clientLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");


        writer = new PrintWriter(MainServer.getInstance().getSocketMap().get(clientIP).getOutputStream(), true);
        // Set action cho các button
        viewScreenButton.setOnAction(event -> {
            writer.println("view");
            new Thread(() -> {
                try {
                    openClientScreen(clientIP);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        lockScreenButton.setOnAction(event -> {
            writer.println("lock");
        });

        messageButton.setOnAction(event -> {
            openChatWindow(clientIP, chatUI);
        });
    }
    private void showImageInView(ImageView clientImageView, String clientIP) throws InterruptedException, IOException {
        //System.out.println("server" + clientIP);
        isRunning = true;
        while (isRunning) {
            try {
                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive(clientIP);

                if (imageBytes != null) {
                    BufferedImage bufferedImage = null;
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

                    try {
                        bufferedImage = ImageIO.read(bis);
                    } catch (IOException e) {
                        System.out.println("Error reading image: " + e.getMessage());
                    }
                    if (bufferedImage != null) {
                        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

                        Platform.runLater(() -> {
                            clientImageView.setImage(fxImage);
                        });
                    } else {
                        System.out.println("Received corrupted image or image format not supported.");
                    }
                } else {
                    // System.out.println("Received null image data.");
                }
                Thread.sleep(10);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    private void openChatWindow(String clientIP, ChatUI chatUI) {
        Platform.runLater(() -> {
            chatUI.launchChatUI("Server Chat with " + clientIP, 1);
        });
    }
    private void openClientScreen(String clientIP) throws InterruptedException {
        Platform.runLater(() -> {
            Stage clientStage = new Stage();
            clientStage.setTitle("Client Screen");

            ImageView clientImageView = new ImageView();
            clientImageView.setFitWidth(1200);
            clientImageView.setFitHeight(680);
            clientImageView.setPreserveRatio(true);

            // Create buttons
            Button mouseControl = new Button("Điều khiển");
            Button screenShot = new Button("Chụp màn hình");
            screenShot.setOnAction(event -> {
                try {
                    captureAndSaveImage(clientImageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            VBox buttonBar = new VBox(10);
            buttonBar.getChildren().addAll(mouseControl, screenShot);
            buttonBar.setAlignment(Pos.CENTER);

            AnchorPane layout = new AnchorPane(clientImageView);
            AnchorPane.setTopAnchor(clientImageView, 0.0);
            AnchorPane.setLeftAnchor(clientImageView, 0.0);
            AnchorPane.setRightAnchor(clientImageView, 0.0);
            AnchorPane.setBottomAnchor(clientImageView, 0.0);

            layout.getChildren().add(buttonBar);
            AnchorPane.setRightAnchor(buttonBar, 10.0);
            AnchorPane.setTopAnchor(buttonBar, 10.0);

            Scene scene = new Scene(layout, 1350, 680);
            clientStage.setScene(scene);
            clientStage.show();

            // Thread to update the image
            Thread imageThread = new Thread(() -> {
                try {
                    showImageInView(clientImageView, clientIP);
                } catch (InterruptedException e) {
                    System.out.println("Image update thread interrupted.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            imageThread.start();

            // Close request event
            clientStage.setOnCloseRequest(event -> {
                writer.println("notView");
                stopImageReceiving();
                imageThread.interrupt();
                System.out.println("Client stage closed, thread interrupted.");
            });
        });
    }
    private void captureAndSaveImage(ImageView imageView) throws IOException {
        WritableImage snapshot = imageView.snapshot(null, null);
        File dir = new File("screenshots");
        if (!dir.exists()) dir.mkdirs();
        String filename = "screenshot_" + System.currentTimeMillis() + ".png";
        File file = new File(dir, filename);

        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        System.out.println("Screenshot saved: " + file.getAbsolutePath());
    }
}
