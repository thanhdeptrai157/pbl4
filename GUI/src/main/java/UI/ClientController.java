package UI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.Client.MainClient;

import java.awt.*;
import java.io.IOException;

public class ClientController {
    private boolean isConnected = false;
    private MainClient client;
    private ChatUI chatUI;
    private Stage stage;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Pane pane;
    @FXML

    public void onConnectButton () throws IOException {

     String ip = ipField.getText().trim();
//        int port = Integer.parseInt(portField.getText().trim());
        //String ip = "localhost";
       // int port = 5001;
       // String ip = ipField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        Stage currentStage = (Stage) pane.getScene().getWindow();
        client = new MainClient(ip, port, stage);
        chatUI = new ChatUI();
        if(client.isConnected()){
            chatUI.setSocket(client.getChatSocket());
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

//            currentStage.close();
            openClientManage();
        }
        else{

        }

    }
    public void openClientManage(){
        Platform.runLater(()->{
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/ClientConnected.fxml"));
                Pane pane = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(pane));
                stage.setAlwaysOnTop(true);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setX(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 240);
                stage.setY(Toolkit.getDefaultToolkit().getScreenSize().getHeight()- 120);

                ClientManagerController controller = loader.getController();
                controller.initialize(chatUI, client.getFileSocket());
                stage.show();
                client.setStage(stage);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
