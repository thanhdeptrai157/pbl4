package com.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField inputName;
    @FXML
    protected void onHelloButtonClick() {
        inputName.getText();
        welcomeText.setText(inputName.getText());

    }
    @FXML
    protected void onCheckClick(){
        inputName.getText();
        welcomeText.setText(inputName.getText());
    }
}