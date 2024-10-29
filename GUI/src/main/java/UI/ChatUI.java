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
    private BufferedReader in;
    private Stage chatStage;
    private ListView<String> chatWindow; // Tạo biến thành viên cho ListView
    private TextField messageInput;

    public ChatUI() {
        chatWindow = new ListView<>(); // Khởi tạo ListView một lần
        messageInput = new TextField(); // Khởi tạo TextField một lần
    }

    public void launchChatUI(String name) {
        // Nếu `chatStage` đã tồn tại và đang hiển thị thì chỉ đưa nó lên trước.
        if (chatStage != null && chatStage.isShowing()) {
            chatStage.toFront();
            return;
        }

        Platform.runLater(() -> {
            // Kiểm tra xem `chatStage` đã được tạo chưa
            if (chatStage == null) {
                chatStage = new Stage();
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

                // Xử lý sự kiện đóng cửa sổ để chỉ ẩn cửa sổ thay vì đóng hoàn toàn
                chatStage.setOnCloseRequest(event -> {
                    event.consume(); // Ngăn chặn hành động đóng mặc định
                    chatStage.hide(); // Ẩn cửa sổ thay vì đóng
                });

                // Luồng nhận tin nhắn từ client
                new Thread(() -> {
                    String message;
                    try {
                        while (!Thread.currentThread().isInterrupted() && (message = in.readLine()) != null) {
                            String finalMessage = message;
                            Platform.runLater(() -> chatWindow.getItems().add("Anonymous: " + finalMessage));
                        }
                    } catch (IOException e) {
                        System.out.println("Error receiving message: " + e.getMessage());
                    }
                }).start();

                BorderPane root = new BorderPane();
                root.setCenter(chatWindow);
                root.setBottom(inputBox);
                Scene scene = new Scene(root, 400, 600);
                chatStage.setTitle(name);
                chatStage.setScene(scene);
            }
            chatStage.show(); // Hiển thị cửa sổ `ChatUI`
        });
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        if (writer == null) { // Khởi tạo `writer` nếu chưa tồn tại
            writer = new PrintWriter(socket.getOutputStream(), true);
        }
        if (in == null) { // Khởi tạo `in` nếu chưa tồn tại
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
    public Socket getSocket(){
        return socket;
    }
}
