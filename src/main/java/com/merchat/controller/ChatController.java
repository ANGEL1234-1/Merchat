package com.merchat.controller;

import com.merchat.Main;
import com.merchat.model.ServerConf;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;

public class ChatController {
    private boolean userInChat = true;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String serverName;
    private Stage stage;
    private ServerConf serverConf;
    private ObjectInputStream objectInput;


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
            serverName = input.readLine();

            if (input.readLine().equals("avaliableSpacesLeft")) {
                stage.setTitle("Merchat - Connected to " + serverName);

                if (input.readLine().equals("serverProtectedByPassword")) {
                    Stage dlgEnterPsdw = new Stage();
                    dlgEnterPsdw.setTitle("Enter server password");
                    dlgEnterPsdw.initStyle(StageStyle.TRANSPARENT);
                    dlgEnterPsdw.setResizable(false);
                    dlgEnterPsdw.initOwner(stage);
                    dlgEnterPsdw.initModality(Modality.WINDOW_MODAL);
                    Label tventerPsdw = new Label("Enter the server's password");
                    PasswordField txtPswd = new PasswordField();
                    Button btndlgEnterPsdw = new Button("Enter");
                    btndlgEnterPsdw.setOnAction(e -> {
                        output.println(txtPswd.getText());
                        dlgEnterPsdw.close();
                    });
                    VBox container = new VBox(tventerPsdw, txtPswd, btndlgEnterPsdw);
                    container.setAlignment(Pos.CENTER);
                    container.setPadding(new Insets(20));
                    container.setSpacing(10);
                    dlgEnterPsdw.setScene(new Scene(container));
                    dlgEnterPsdw.setWidth(250);
                    dlgEnterPsdw.setHeight(150);
                    dlgEnterPsdw.show();
                }

                new Thread(() -> {
                    String msg;

                    while (userInChat) {
                        try {
                            msg = input.readLine();
                            if (msg.equals(null)) {
                                userInChat = false;
                            } else {
                                tvChat.appendText(msg + "\n");
                            }
                        } catch (Exception e) {
                            userInChat = false;
                            tvChat.appendText("> ERROR: CONNECTION WITH SERVER LOST\n");
                            System.out.println(e);
                        }
                    }

                    try {
                        input.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    output.close();
                }).start();
            } else {
                socket.close();
                tvChat.appendText("> ERROR: SERVER IS FULL");
            }
        } catch (IOException e) {
            try {
                userInChat = false;
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            System.out.println(e);
            tvChat.appendText("> ERROR: CONNECTION WITH SERVER LOST\n");
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
        stage.setTitle("Merchat");
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            FXMLLoader fxmlManualConnectionLoader = new FXMLLoader(Main.class.getResource("view/manual-connection.fxml"));
            Scene sceneChat = new Scene(fxmlManualConnectionLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.close();
            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
