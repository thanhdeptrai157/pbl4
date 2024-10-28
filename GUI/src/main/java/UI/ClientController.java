package UI;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Client.MainClient;

import java.awt.*;
import java.io.IOException;

public class ClientController {
    private Stage stage;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    public void onConnectButton (){
        String ip = ipField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        MainClient client = new MainClient(ip, port);
        ChatUI chatUI = new ChatUI();
        if(client.isConnected()){
            new Thread(()->{
                try {
                    chatUI.launchChatUI("Client");
                    chatUI.setSocket(client.getChatSocket());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            new Thread(()->{
                try {
                    client.commandFromServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
