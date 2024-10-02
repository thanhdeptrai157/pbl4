package UI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatUI {
    private Socket socket;
    private PrintWriter writer;
    public ChatUI() {

    }
    public void launchChatUI(String name) {
        Platform.runLater(() -> {
            Stage chatStage = new Stage();
            ListView<String> chatWindow = new ListView<>();
            TextField messageInput = new TextField();
            messageInput.setPromptText("Nhập tin nhắn...");
            Button sendButton = new Button("Gửi");
            HBox inputBox = new HBox(10, messageInput, sendButton);
            sendButton.setOnAction(e -> {
                String message = messageInput.getText();
                if (!message.isEmpty()) {
                    chatWindow.getItems().add("You: " + message);
                    writer.println(message);
                    messageInput.clear();
                }
            });
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(()->{
                    String message;
                    while(true){
                        try {
                            if (((message = in.readLine()) != null)){
                                String finalMessage = message;
                                Platform.runLater(()->{
                                    chatWindow.getItems().add("Anonymous: " + finalMessage);
                                });
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BorderPane root = new BorderPane();
            root.setCenter(chatWindow);
            root.setBottom(inputBox);
            Scene scene = new Scene(root, 400, 600);
            chatStage.setTitle(name);
            chatStage.setScene(scene);
            chatStage.show();
        });
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        writer = new PrintWriter(socket.getOutputStream(), true);
    }
}

