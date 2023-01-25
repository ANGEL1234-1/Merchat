package com.merchat.controller;

import com.merchat.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {
    private Stage stage;


    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtUsername;
    @FXML
    private Label tvLogIn;


    public void onJoinButtonClick() {
        if (txtUsername.getText().equals("")) {
            tvLogIn.setText("Username can't be empty");
        } else if (txtUsername.getText().contains(" ")) {
            tvLogIn.setText("Username can't contain spaces");
        } else {
            try {
                stage = (Stage) root.getScene().getWindow();

                stage.getProperties().put("username", txtUsername.getText());

                FXMLLoader fxmlJoinTypeLoader = new FXMLLoader(Main.class.getResource("view/join-type.fxml"));
                Scene sceneChat = new Scene(fxmlJoinTypeLoader.load());

                stage.setScene(sceneChat);
                stage.show();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void onHostButtonClick() {
        if (txtUsername.getText().equals("")) {
            tvLogIn.setText("Username can't be empty");
        } else {
            try {
                stage = (Stage) root.getScene().getWindow();

                stage.getProperties().put("username", txtUsername.getText());

                FXMLLoader fxmlHostTypeLoader = new FXMLLoader(Main.class.getResource("view/host-type.fxml"));
                Scene sceneHostType = new Scene(fxmlHostTypeLoader.load());

                stage.close();
                stage.setScene(sceneHostType);
                stage.show();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
