package com.merchat.controller;

import com.merchat.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class JoinTypeController {
    public AnchorPane root;

    public void onBackButtonClick() {
        try {
            FXMLLoader fxmlLogInLoader = new FXMLLoader(Main.class.getResource("view/log-in.fxml"));
            Scene sceneChat = new Scene(fxmlLogInLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void onManualButtonClick() {
        try {
            FXMLLoader fxmlManualConnectionLoader = new FXMLLoader(Main.class.getResource("view/manual-connection.fxml"));
            Scene sceneChat = new Scene(fxmlManualConnectionLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
