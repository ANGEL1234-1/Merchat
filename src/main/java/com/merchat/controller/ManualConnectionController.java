package com.merchat.controller;

import com.merchat.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManualConnectionController {
    private String ip;
    private final int PORT = 54321;


    private final String ZERO_TO_255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
    private final Pattern PATTERN_IP = Pattern.compile("(" + ZERO_TO_255 + "\\.){3}" + ZERO_TO_255);
    private Matcher matcherIp;
    private Socket socket;
    private Stage stage;


    @FXML
    private Label tvLogIn;
    @FXML
    private TextField txtIp;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnConnect;
    @FXML
    private AnchorPane root;
    
    
    @FXML
    protected void onConnectButtonClick() {
        ip = txtIp.getText();
        matcherIp = PATTERN_IP.matcher(ip);

        if (matcherIp.matches()) {
            tvLogIn.setText("");
            btnConnect.setDisable(true);
            btnBack.setDisable(true);
            createConnection();
        } else {
            tvLogIn.setText("The IP introduced is not correct!");
        }
    }

    private void createConnection() {
        // Create a thread to perform this task so the application doesn't freeze while making the connection
        new Thread(() -> {
            try {
                socket = new Socket(ip, PORT);

                Platform.runLater(() -> {
                    try {
                        FXMLLoader fxmlChatLoader = new FXMLLoader(Main.class.getResource("view/chat.fxml"));
                        Scene sceneChat = new Scene(fxmlChatLoader.load());
                        stage = (Stage) root.getScene().getWindow();

                        stage.close();
                        stage.setScene(sceneChat);
                        stage.show();

                        ((ChatController)fxmlChatLoader.getController()).start(socket);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    tvLogIn.setText("Could not connect...");
                    System.out.println(e);
                });
            } finally {
                Platform.runLater(() -> {
                    btnConnect.setDisable(false);
                    btnBack.setDisable(false);
                });
            }
        }).start();
    }

    public void onBackButtonClick() {
        try {
            FXMLLoader fxmlChatLoader = new FXMLLoader(Main.class.getResource("view/join-type.fxml"));
            Scene sceneChat = new Scene(fxmlChatLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}