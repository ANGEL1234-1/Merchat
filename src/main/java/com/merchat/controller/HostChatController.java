package com.merchat.controller;

import com.merchat.Main;
import com.merchat.model.ClientThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HostChatController {
    private boolean hosting = true;
    private boolean treeIsVisible = false;
    private String username;
    private ServerSocket serverSocket;
    private ArrayList<ClientThread> clients = new ArrayList<>();


    @FXML
    private AnchorPane root;
    @FXML
    private TextArea tvChat;
    @FXML
    private TextField txtChat;
    @FXML
    private Accordion acrdServerTools;


    public ArrayList<ClientThread> getClients() {
        return clients;
    }

    public void start() {

        try {
            username = root.getScene().getWindow().getProperties().get("username").toString();

            serverSocket = new ServerSocket(54321);
            tvChat.appendText("> Server online\n");
            new Thread(() -> {
                while (hosting) {
                    Socket socket;
                    try {
                        socket = serverSocket.accept();
                        clients.add(new ClientThread(this, socket));
                        clients.get(clients.size() - 1).start();
                    } catch (IOException e) {
                        System.out.println("No se pudo aceptar la conexion");
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Server error " + e);
        }
    }


    public void onTxtChatKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (!txtChat.getText().equals("")) {
                if (!txtChat.getText().equals("\\exit")) {
                    tvChat.appendText(txtChat.getText() + "\n");
                    broadcast(username, txtChat.getText());
                    txtChat.setText("");
                } else {
                    onBtnGoBackClicked();
                }
            }
        }
    }

    public void onBtnGoBackClicked() {
        hosting = false;
        try {
            broadcast("> SERVER OFFLINE");
            serverSocket.close();
        } catch (IOException | NullPointerException e) {
            System.out.println(e);
            tvChat.appendText("> ERROR: COULD NOT HOST SERVER\n");
        }

        try {
            FXMLLoader fxmlChatLoader = new FXMLLoader(Main.class.getResource("view/log-in.fxml"));
            Scene sceneChat = new Scene(fxmlChatLoader.load());
            Stage stage = (Stage) root.getScene().getWindow();

            stage.close();
            stage.setScene(sceneChat);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void onBtnServerToolsClick() {
        if (treeIsVisible) {
            acrdServerTools.setManaged(false);
            acrdServerTools.setVisible(false);
            treeIsVisible = false;
        } else {
            acrdServerTools.setManaged(true);
            acrdServerTools.setVisible(true);
            treeIsVisible = true;
        }
    }

    public void broadcast(ClientThread user, String uname, String msg) {
        tvChat.appendText(uname + ": " + msg + "\n");
        for (ClientThread client: clients) {
            if (!client.equals(user)) {
                client.getOutput().println(uname + ": " + msg);
            }
        }
    }

    public void broadcast(String uname, String msg) {
        for (ClientThread client: clients) {
            client.getOutput().println(uname + ": " + msg);
        }
    }

    public void broadcast(String msg) {
        tvChat.appendText(msg + "\n");
        for (ClientThread client: clients) {
            client.getOutput().println(msg);
        }
    }
}
