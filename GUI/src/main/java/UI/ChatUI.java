package UI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.Network.Encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class ChatUI {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader in;
    private Stage chatStage;
    private ListView<Pane> chatWindow; // Sử dụng Pane thay vì HBox để căn chỉnh toàn bộ tin nhắn
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
                        displayMessage("You: " + message, Pos.BASELINE_RIGHT);
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
                            Platform.runLater(() -> displayMessage(id == 1 ? "Client: " + finalMessage : "Server: " + finalMessage, Pos.BASELINE_LEFT));
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

    private void displayMessage(String message, Pos position) {
//        HBox messageBox = new HBox();
//
//        Text text = new Text(message);
//
//        text.wrappingWidthProperty().bind(chatWindow.widthProperty().subtract(100));
//        text.setFill(Color.WHITE);
//        messageBox.getChildren().add(text);
//
//        // Đặt màu nền cho tin nhắn theo vị trí
//        if (position == Pos.BASELINE_RIGHT) {
//            messageBox.setStyle("-fx-background-color: #11141c; -fx-padding: 10; -fx-background-radius: 10;");
//        } else {
//            messageBox.setStyle("-fx-background-color: #0f7bd9; -fx-padding: 10; -fx-background-radius: 10;");
//        }
//
//        Pane pane = new Pane(messageBox);  // Đặt HBox trong một Pane để kiểm soát vị trí
//        if (position == Pos.BASELINE_RIGHT) {
//            pane.setMaxWidth(Double.MAX_VALUE);
//            messageBox.setAlignment(Pos.BASELINE_RIGHT);
//            messageBox.setLayoutX(pane.getWidth() - messageBox.getWidth()); // Đẩy sát về bên phải
//        } else {
//            messageBox.setAlignment(Pos.BASELINE_LEFT);
//        }
//
//        chatWindow.getItems().add(pane);
        // Tạo Text hiển thị tin nhắn
        Text text = new Text(message);
        text.setFill(Color.WHITE);

// Tạo TextFlow để tự động điều chỉnh kích thước
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(10));
        textFlow.setMaxWidth(chatWindow.getWidth() / 2); // Giới hạn chiều rộng tối đa
        textFlow.setStyle("-fx-background-radius: 10;");

// Đặt màu nền tùy theo vị trí
        if (position == Pos.BASELINE_RIGHT) {
            textFlow.setStyle("-fx-background-color: #11141c; -fx-background-radius: 10; -fx-padding: 10;");
        } else {
            textFlow.setStyle("-fx-background-color: #0f7bd9; -fx-background-radius: 10; -fx-padding: 10;");
        }

// Tạo HBox chứa TextFlow để căn chỉnh vị trí
        HBox container = new HBox(textFlow);
        if (position == Pos.BASELINE_RIGHT) {
            container.setAlignment(Pos.CENTER_RIGHT); // Tin nhắn gửi (bên phải)
        } else {
            container.setAlignment(Pos.CENTER_LEFT);  // Tin nhắn nhận (bên trái)
        }

// Thêm container vào danh sách chat
        chatWindow.getItems().add(container);
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
