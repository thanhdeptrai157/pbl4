package UI;
import javafx.application.Platform;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Network.SenderTransfer;
import org.Server.ClientConnectionListener;
import org.Server.MainServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ServerController implements ClientConnectionListener {

    @FXML
    private Pane mainLayout;
    @FXML
    private Button sendAssignmentButton;
    private final Map<String, ChatUI> clientChats = new HashMap<>();
    private File selectedFile;
    private int clientCounter = 0;
    private Pane dashBoard;
    private Pane home;
    @FXML
    public void initialize() throws IOException {
        dashBoard = new Pane();
        home = new Pane();
        new Thread(() -> {
            try {
                MainServer.getInstance().startServer(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    @FXML
    private void handleSendAssignment() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp bài tập");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tất cả các tệp", "*.*"),
                new FileChooser.ExtensionFilter("Tệp văn bản", "*.txt", "*.pdf", "*.docx")
        );
        Stage stage = (Stage) sendAssignmentButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            System.out.println("Đã chọn tệp: " + selectedFile.getAbsolutePath());
            for(Socket socket : MainServer.getInstance().getSocketMapFile().values() ) {
                SenderTransfer senderFile = new SenderTransfer(selectedFile, socket);
                senderFile.start();
                try {
                    senderFile.join();
                    System.out.println("Đã gửi file: " + selectedFile.getName());
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

        } else {
            System.out.println("Không có tệp nào được chọn.");
        }
    }
    private void addClientIndicator(String clientIP, ChatUI chatUI) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/ClientIndicator.fxml"));
            double xOffset = 50 + (clientCounter % 3) * 400;
            double yOffset = 50 + (clientCounter / 3) * 300;
            Parent clientPane = loader.load();
            clientCounter++;
            ClientIndicatorController clientIndicatorController = loader.getController();
            clientIndicatorController.initialize(clientIP, chatUI, clientCounter);

            clientPane.setLayoutX(xOffset);
            clientPane.setLayoutY(yOffset);

            home.getChildren().add(clientPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClientConnected(String clientIP) {
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
            handleShowHome();
            addClientIndicator(clientIP, chatUI);
        });
    }

    public void handleSendToastMessage() {
        Stage message = new Stage();
        message.setTitle("Nhap thong bao");

        TextField textfield = new TextField();
        Button send = new Button("Gửi");
        send.setOnAction(event->{
            String mes = textfield.getText();
            try {
                for(Socket socket : MainServer.getInstance().getSocketMap().values()){
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println("Mess: " + mes);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(textfield, send);
        layout.setAlignment(Pos.CENTER);


        Scene scene = new Scene(layout, 300, 150);
        message.setScene(scene);
        message.show();
    }
    private void showImageInView(ImageView clientImageView, int numClient) throws InterruptedException, IOException {
        boolean isRunning = true;
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
    public void handleLockAll(){
    try {
        for (Socket socket : MainServer.getInstance().getSocketMap().values()) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("lockall");
        }
    }
    catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleShowDashBoard() {
        try {
            for(Socket socket : MainServer.getInstance().getSocketMap().values()) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("view");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(int i = 0; i < clientCounter; i++){
            Pane clientPane = new Pane();
            ImageView imageView = new ImageView();
            //imageView.setImage(new Image("D:\\pbl4\\GUI\\src\\main\\resources\\Style\\cmp.jpg"));
            imageView.setFitWidth(400);
            imageView.setFitHeight(200);
            double xOffset = 50 + (i % 3) * 400;
            double yOffset = 50 + (i / 3) * 300;
            clientPane.getChildren().add(imageView);
            clientPane.setLayoutX(xOffset);
            clientPane.setLayoutY(yOffset);
            dashBoard.getChildren().add(clientPane);
            int finalI = i;
            new Thread(() -> {
                try {
                    showImageInView(imageView, finalI+1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(dashBoard);
    }

    public void handleShowHome() {
        try {
            for(Socket socket : MainServer.getInstance().getSocketMap().values()) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("notView");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainLayout.getChildren().clear();
        mainLayout.getChildren().add(home);
    }
}

