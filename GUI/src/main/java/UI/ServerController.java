package UI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Network.SenderTransfer;
import org.Server.ClientConnectionListener;
import org.Server.MainServer;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ServerController implements ClientConnectionListener {

    @FXML
    private AnchorPane mainLayout;
    @FXML
    private Button sendAssignmentButton;
    private final Map<String, ChatUI> clientChats = new HashMap<>();
    private File selectedFile;
    private int clientCounter = 0;
    @FXML
    public void initialize() throws IOException {
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


        AnchorPane.setTopAnchor(clientPane, yOffset);
        AnchorPane.setLeftAnchor(clientPane, xOffset);

        mainLayout.getChildren().add(clientPane);

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
}
