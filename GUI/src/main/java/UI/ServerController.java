package UI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.Server.ClientConnectionListener;
import org.Server.MainServer;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ServerController implements ClientConnectionListener {

    @FXML
    private AnchorPane mainLayout;
    private ChatUI chatUI;
    private final Map<String, ChatUI> clientChats = new HashMap<>();
    private int clientCounter = 0;
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
}
