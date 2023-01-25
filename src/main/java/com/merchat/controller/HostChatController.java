package com.merchat.controller;

import com.merchat.Main;
import com.merchat.model.ClientThread;
import com.merchat.model.ServerConf;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;;
import javafx.stage.Modality;
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
    private final ArrayList<ClientThread> clients = new ArrayList<>();
    private final ContextMenu userOptions = new ContextMenu();
    private final MenuItem kick = new MenuItem("Kick user");
    private final MenuItem whisper = new MenuItem("Whisper");
    private Label selectedLabel;
    private ServerConf serverConf;


    @FXML
    private AnchorPane root;
    @FXML
    private TextArea tvChat;
    @FXML
    private TextField txtChat;
    @FXML
    private Accordion acrdServerTools;
    @FXML
    public RadioButton rbtSpeak;
    @FXML
    public RadioButton rbtBroadcast;
    @FXML
    private VBox paneUsers;


    public void start(ServerConf serverConf) {
        try {
            this.serverConf = serverConf;

            username = root.getScene().getWindow().getProperties().get("username").toString();
            userOptions.getItems().add(kick);
            userOptions.getItems().add(whisper);

            kick.setOnAction(e -> {
                whisper(clients.get(paneUsers.getChildren().indexOf(selectedLabel)), "You have been kicked");
                clients.get(paneUsers.getChildren().indexOf(selectedLabel)).close();
            });
            whisper.setOnAction(e -> {
                Stage stage = (Stage) root.getScene().getWindow();

                Stage dlgWhisper = new Stage();
                dlgWhisper.setTitle("Whisper to " + clients.get(paneUsers.getChildren().indexOf(selectedLabel)).getUname());
                dlgWhisper.setResizable(false);
                dlgWhisper.initOwner(stage);
                dlgWhisper.initModality(Modality.WINDOW_MODAL);
                Label tvdlgWhisper = new Label("What do you want to whisper");
                TextField txtPswd = new TextField();
                Button btndlgWhisper = new Button("Send");
                btndlgWhisper.setOnAction(event -> {
                    whisper(clients.get(paneUsers.getChildren().indexOf(selectedLabel)), txtPswd.getText());
                    dlgWhisper.close();
                });
                VBox container = new VBox(tvdlgWhisper, txtPswd, btndlgWhisper);
                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(20));
                container.setSpacing(10);
                dlgWhisper.setScene(new Scene(container));
                dlgWhisper.setWidth(250);
                dlgWhisper.setHeight(150);
                dlgWhisper.show();
            });

            serverSocket = new ServerSocket(this.serverConf.getPort());
            tvChat.appendText("> Server online\n");
            new Thread(() -> {
                while (hosting) {
                    Socket socket;
                    try {
                        socket = serverSocket.accept();

                        clients.add(new ClientThread(this, socket));
                        clients.get(clients.size() - 1).start();
                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                System.out.println(e);
                            }
                            addUser();
                        });
                    } catch (IOException e) {
                        System.out.println("No se pudo aceptar la conexion");
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Server error " + e);
        }
    }


    public ArrayList<ClientThread> getClients() {
        return clients;
    }

    public VBox getPaneUsers() {
        return paneUsers;
    }

    public ServerConf getServerConf() {
        return serverConf;
    }

    private void addUser() {
        paneUsers.getChildren().add(new Label(clients.get(clients.size() - 1).getUname()));
        ((Label) paneUsers.getChildren().get(paneUsers.getChildren().size() - 1)).setContextMenu(userOptions);
        clients.get(clients.size() - 1).setLabel((Label) paneUsers.getChildren().get(paneUsers.getChildren().size() - 1));

        paneUsers.getChildren().get(paneUsers.getChildren().size() - 1).setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                selectedLabel = (Label) e.getSource();
            }
        });
    }


    public void onTxtChatKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (!txtChat.getText().equals("")) {
                if (rbtSpeak.isSelected()) {
                    tvChat.appendText(txtChat.getText() + "\n");
                    broadcast(username, txtChat.getText());
                } else {
                    tvChat.appendText("SERVER: " + txtChat.getText().toUpperCase() + "\n");
                    broadcast("SERVER", txtChat.getText().toUpperCase());
                }
                txtChat.setText("");
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
            FXMLLoader fxmlLogInLoader = new FXMLLoader(Main.class.getResource("view/log-in.fxml"));
            Scene sceneChat = new Scene(fxmlLogInLoader.load());
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

    public void whisper(ClientThread clientThread, String msg) {
        tvChat.appendText("[Whispered to " + clientThread.getUname() + ": " + msg + "]\n");
        clientThread.getOutput().println("[Server whispered to you: " + msg + "]");
    }
}
