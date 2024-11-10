package UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Network.SenderTransfer;
import java.io.File;
import java.io.IOException;
import java.net.Socket;


public class ClientManagerController {
    private ChatUI chatUI;
    private Socket sk;
    private File selectedFile;
    @FXML
    private Button buttonSend;
    @FXML
    public void initialize(ChatUI chatUI, Socket sk){
        this.chatUI = chatUI;
        this.sk = sk;
    }
    public void onChatClick() {
        new Thread(()->{
            chatUI.launchChatUI("Client", 2);
        }).start();
    }
    @FXML
    private void handleSendAssignment() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp bài tập");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tất cả các tệp", "*.*"),
                new FileChooser.ExtensionFilter("Tệp văn bản", "*.txt", "*.pdf", "*.docx")
        );
        Stage stage = (Stage) buttonSend.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            System.out.println("Đã chọn tệp: " + selectedFile.getAbsolutePath());
                SenderTransfer senderFile = new SenderTransfer(selectedFile, sk);
                senderFile.start();
                try {
                    senderFile.join();
                    System.out.println("Đã gửi file: " + selectedFile.getName());
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
            }
        } else {
            System.out.println("Không có tệp nào được chọn.");
        }
    }
}
