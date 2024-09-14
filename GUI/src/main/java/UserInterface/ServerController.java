package UserInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.module.MainServer;

public class ServerController {
    private MainServer mainServer;
    @FXML

    private ImageView imageView;
    @FXML
    public void init(){

    }
    @FXML
    public void onClick() {

        imageView.setImage(new Image("C:\\Users\\CONG THANH\\Downloads\\Splash art Crying banana cat Final.jpg"));
    }

    @FXML
    public void onClick2() {
        imageView.setImage(new Image("C:\\Users\\CONG THANH\\Downloads\\"));
    }
//    @FXML
//    protected void onClick(){
//        imageView.setImage(new Image("Downloads\\Splash art Crying banana cat Final.jpg"));
//    }
}
