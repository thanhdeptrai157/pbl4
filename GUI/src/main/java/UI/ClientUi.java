package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.module.MainClient;

public class ClientUi extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientUi.class.getResource("Client.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 350);
        stage.setTitle("Client Xin Chao");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        ClientController controller = fxmlLoader.getController();
        controller.setStage(stage);

    }
    public static void launchUI(String[] args) {
        launch(args);
    }
}
