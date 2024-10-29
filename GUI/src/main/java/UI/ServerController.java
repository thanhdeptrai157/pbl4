package UI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.Server.ClientConnectionListener;
import org.Server.MainServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ServerController implements ClientConnectionListener {

    @FXML
    private AnchorPane mainLayout;
    private ChatUI chatUI;
    private final Map<String, ChatUI> clientChats = new HashMap<>();
    private volatile boolean isRunning = true;

    public void stopImageReceiving() {
        isRunning = false;
    }
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
    private void openClientScreen(String clientIP) throws InterruptedException {
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

            Scene scene = new Scene(layout, 1200, 780);
            clientStage.setScene(scene);
            clientStage.show();
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
            clientStage.setOnCloseRequest(event -> {
                try {
                    PrintWriter writer = new PrintWriter(MainServer.getInstance().getSocketMap().get(clientIP).getOutputStream(), true);
                    writer.println("notView");
                    stopImageReceiving();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imageThread.interrupt();
                System.out.println("Client stage closed, thread interrupted.");
            });
        });
    }

    private void showImageInView(ImageView clientImageView, String clientIP) throws InterruptedException, IOException {
        System.out.println("server" + clientIP);
        isRunning = true;
        while (isRunning) {
            try {
                byte[] imageBytes = MainServer.getInstance().getReceivePacket().receive("172.1.1.1");

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

    private void openChatWindow(String clientIP) {
        Platform.runLater(() -> {
            ChatUI chatUI = clientChats.get(clientIP);
            if (chatUI == null) {
                chatUI = new ChatUI();
                clientChats.put(clientIP, chatUI);
                try {
                    chatUI.setSocket(MainServer.getInstance().getSocketMapChat().get(clientIP));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            chatUI.launchChatUI("Server Chat with " + clientIP);
        });
    }

    private int clientCounter = 0;

    private void addClientIndicator(String clientIP) throws FileNotFoundException {
        Rectangle clientIndicator = new Rectangle(350, 250);
        clientIndicator.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.LIGHTSTEELBLUE),
                new Stop(1, Color.STEELBLUE)
        ));
        clientIndicator.setStroke(Color.DARKSLATEBLUE);
        clientIndicator.setArcHeight(10);
        clientIndicator.setArcWidth(10);

        // Load computer icon
        ImageView computerIcon = new ImageView(new Image(new FileInputStream("D:\\Intelliji Program\\ProjectPBL4\\pbl4\\GUI\\src\\main\\resources\\Style\\cmp.jpg")));
        computerIcon.setFitWidth(120);
        computerIcon.setFitHeight(120);

        // Client IP label
        Label clientLabel = new Label("Client IP: " + clientIP);
        clientLabel.setTextFill(Color.BLACK);
        clientLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox labelBox = new HBox(10, clientLabel);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        // Button setup
        Button viewScreenButton = new Button("Xem màn hình");
        Button lockScreenButton = new Button("Lock màn hình");
        Button messageButton = new Button("Chat");

        // Set uniform button style and width
        double buttonWidth = 160;
        viewScreenButton.setPrefWidth(buttonWidth);
        lockScreenButton.setPrefWidth(buttonWidth);
        messageButton.setPrefWidth(buttonWidth);
        viewScreenButton.setStyle("-fx-font-size: 16;");
        lockScreenButton.setStyle("-fx-font-size: 16;");
        messageButton.setStyle("-fx-font-size: 16;");

        // Button actions
        viewScreenButton.setOnAction(event -> {
            try {
                PrintWriter writer = new PrintWriter(MainServer.getInstance().getSocketMap().get(clientIP).getOutputStream(), true);
                writer.println("view");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new Thread(() -> {
                try {
                    openClientScreen(clientIP);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        lockScreenButton.setOnAction(event -> {
            try {
                PrintWriter writer = new PrintWriter(MainServer.getInstance().getSocketMap().get(clientIP).getOutputStream(), true);
                writer.println("lock");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        messageButton.setOnAction(event -> openChatWindow(clientIP));

        // Layout for buttons and icon on the right
        VBox buttonBox = new VBox(10, viewScreenButton, lockScreenButton, messageButton);
        buttonBox.setAlignment(Pos.TOP_LEFT);
        HBox mainContentBox = new HBox(10, buttonBox, computerIcon);
        mainContentBox.setAlignment(Pos.CENTER_LEFT);

        VBox clientBox = new VBox(10, labelBox, mainContentBox);
        clientBox.setPadding(new Insets(20));
        clientBox.setAlignment(Pos.TOP_LEFT);

        StackPane clientPane = new StackPane(clientIndicator, clientBox);

        double xOffset = 50 + (clientCounter % 3) * 400;
        double yOffset = 50 + (clientCounter / 3) * 400;

        AnchorPane.setTopAnchor(clientPane, yOffset);
        AnchorPane.setLeftAnchor(clientPane, xOffset);

        mainLayout.getChildren().add(clientPane);
        clientCounter++; // Increment counter after adding each client

        System.out.println("Client IP: " + clientIP);
    }




    @Override
    public void onClientConnected(String clientIP) {
        Platform.runLater(() -> {
            try {
                addClientIndicator(clientIP);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
