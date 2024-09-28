package UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import org.Server.MainServer;
//import org.module.MainServer;

import java.io.IOException;

public class ServerController {
    @FXML

    private ImageView imageView;
    @FXML
    public void initialize() {
        new Thread(()->{
            try {
                if(MainServer.getInstance().waitForClient()){
                    String clientIP = MainServer.getInstance().getClientSocket().getInetAddress().getHostAddress();
                    ChatUI chatUI = new ChatUI();
                   // Platform.runLater(() -> showClientConnectedDialog(clientIP));
                    Platform.runLater(()->{
                        try {
                            chatUI.setSocket(MainServer.getInstance().getClientSocket());
                            chatUI.launchChatUI("Server");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    @FXML
    public void onClick() throws IOException {
//
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
