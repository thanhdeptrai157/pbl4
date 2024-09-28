package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class ServerUI extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerUI.class.getResource("Server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Server Chao Xin!");
        primaryStage.show();
    }

    public static void main(String [] args){
        launch(args);
    }
}
