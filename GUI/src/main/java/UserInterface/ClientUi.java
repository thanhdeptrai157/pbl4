package UserInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientUi extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientUi.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 300);
        stage.setTitle("Client Xin Chao");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public static void launchUI(String[] args) {
        launch(args);
    }
}
