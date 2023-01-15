package com.merchat.controller;

import com.merchat.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatController {
    private boolean userInChat = true;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;


    private Stage stage;


    @FXML
    private AnchorPane root;
    @FXML
    private TextArea tvChat;
    @FXML
    private TextField txtChat;


    public void start(Socket socket) {
        this.socket = socket;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            stage = (Stage) root.getScene().getWindow();
            output.println(stage.getProperties().get("username"));

            new Thread(() -> {
                while (userInChat) {
                    try {
                        tvChat.appendText(input.readLine() + "\n");
                    } catch (IOException | NullPointerException e) {
                        userInChat = false;
                        tvChat.appendText("ERROR: CONNECTION WITH SERVER LOST");
                        System.out.println(e);
                    }
                }
            }).start();
        } catch (IOException e) {
            try {
                userInChat = false;
                socket.close();
            } catch (IOException ex) {
                System.out.println(e);
            }
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }


    public void onTxtChatKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (!txtChat.getText().equals("")) {
                if (!txtChat.getText().equals("\\exit")) {
                    tvChat.appendText(txtChat.getText() + "\n");
                    output.println(txtChat.getText());
                    txtChat.setText("");
                } else {
                    onBtnGoBackClicked();
                }
            }
        }
    }

    public void onBtnGoBackClicked() {
        userInChat = false;
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            FXMLLoader fxmlChatLoader = new FXMLLoader(Main.class.getResource("view/manual-connection.fxml"));
            Scene sceneChat = new Scene(fxmlChatLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.close();
            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
