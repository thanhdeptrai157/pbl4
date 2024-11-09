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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.Server.MainServer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientIndicatorController {
    private volatile boolean isRunning = true;
    private ChatUI chatUI;
    private int numClient;
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
    public void initialize(String clientIP, ChatUI chatUI, int numClient) throws IOException {
        clientLabel.setText("Client IP: " + clientIP);
        clientLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        this.numClient = numClient;
        writer = new PrintWriter(MainServer.getInstance().getSocketMap().get(clientIP).getOutputStream(), true);
        viewScreenButton.setOnAction(event -> {
            writer.println("view");
            new Thread(() -> {
                try {
                    openClientScreen(numClient);
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
    private void showImageInView(ImageView clientImageView, int numClient) throws InterruptedException, IOException {
        isRunning = true;
        while (isRunning) {
            try {
                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive(numClient);
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
    private void openClientScreen(int numClient) throws InterruptedException {
        Platform.runLater(() -> {
            Stage clientStage = new Stage();
            clientStage.setTitle("Client Screen");

            ImageView clientImageView = new ImageView();
            clientImageView.setFocusTraversable(true);
            clientImageView.setFitWidth(1200);
            clientImageView.setFitHeight(680);
            clientImageView.setPreserveRatio(true);

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
            AtomicBoolean isActive = new AtomicBoolean(false);
            mouseControl.setOnAction(event -> {
                isActive.set(!isActive.get());
                if(isActive.get()){
                    clientImageView.setOnMouseMoved(e -> {
                        double mouseX = e.getSceneX();
                        double mouseY = e.getSceneY();
                        String clickType = "none";
                        writer.println("move " + mouseX + " " + mouseY + " " + clickType);
                    });
                    clientImageView.setOnMouseClicked(e -> {
                        clientImageView.requestFocus();
                        double mouseX = e.getSceneX();
                        double mouseY = e.getSceneY();
                        String clickType = "none";
                        if(e.getButton() == MouseButton.PRIMARY){
                            clickType = "left";
                        } else if(e.getButton() == MouseButton.SECONDARY){
                            clickType = "right";
                        }
                        writer.println("click " + mouseX + " " + mouseY + " " + clickType);
                    });
                    clientImageView.setOnKeyPressed(e -> {
                        int keyCode = e.getCode().getCode();
                        boolean shift = e.isShiftDown();
                        boolean ctrl = e.isControlDown();
                        boolean alt = e.isAltDown();
                        writer.println("type " + keyCode + " " + shift + " " + ctrl + " " + alt);
                    });
                    clientImageView.setOnScroll(e->{
                        int delta = (int) e.getDeltaY();
                        System.out.println(delta);
                        writer.println("scroll " + delta);
                    });
                }
                else{
                    clientImageView.setOnKeyTyped(null);
                    clientImageView.setOnMouseMoved(null);
                    clientImageView.setOnMouseClicked(null);
                }
            });
            Thread imageThread = new Thread(() -> {
                try {
                    showImageInView(clientImageView, numClient);
                } catch (InterruptedException e) {
                    System.out.println("Image update thread interrupted.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            imageThread.start();
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