package UI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.Network.Encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatUI {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader in;
    private Stage chatStage;
    private ListView<String> chatWindow;
    private TextField messageInput;

    public ChatUI() {
        chatWindow = new ListView<>();
        messageInput = new TextField();
    }

    public void launchChatUI(String name, int id) {
        if (chatStage != null && chatStage.isShowing()) {
            chatStage.toFront();
            return;
        }

        Platform.runLater(() -> {
            if (chatStage == null) {
                chatStage = new Stage();
                messageInput.setPromptText("Nhập tin nhắn...");
                Button sendButton = new Button("Gửi");
                HBox inputBox = new HBox(10, messageInput, sendButton);
                sendButton.setOnAction(e -> {
                    String message = messageInput.getText();
                    if (!message.isEmpty()) {
                        chatWindow.getItems().add("You: " + message);
                        try {
                            writer.println(Encode.encode(message));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        messageInput.clear();
                    }
                });
                chatStage.setOnCloseRequest(event -> {
                    event.consume();
                    chatStage.hide();
                });
                new Thread(() -> {
                    String message;
                    try {
                        while (!Thread.currentThread().isInterrupted() && (message = in.readLine()) != null) {
                            String finalMessage = Encode.decode(message);
                            Platform.runLater(() -> chatWindow.getItems().add(id == 1? "Client: "+ finalMessage : "Server: " + finalMessage));
                        }
                    } catch (IOException e) {
                        System.out.println("Error receiving message: " + e.getMessage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                BorderPane root = new BorderPane();
                root.setCenter(chatWindow);
                root.setBottom(inputBox);
                Scene scene = new Scene(root, 400, 600);
                chatStage.setTitle(name);
                chatStage.setScene(scene);
            }
            chatStage.show();
        });
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        if (writer == null) {
            writer = new PrintWriter(socket.getOutputStream(), true);
        }
        if (in == null) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
    public Socket getSocket(){
        return socket;
    }
}
