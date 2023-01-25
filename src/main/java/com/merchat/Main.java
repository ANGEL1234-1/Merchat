package com.merchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLogInLoader = new FXMLLoader(Main.class.getResource("view/log-in.fxml"));

        Scene sceneLogIn = new Scene(fxmlLogInLoader.load());

        stage.setTitle("Merchat");
        stage.getIcons().add(new Image(getClass().getResource("images/icon.png").toExternalForm()));
        stage.setScene(sceneLogIn);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}