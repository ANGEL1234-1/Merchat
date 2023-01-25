package com.merchat.controller;

import com.merchat.Main;
import com.merchat.model.ServerConf;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HostTypeController {
    private int port;
    private final int DEFAULT_PORT = 54321;


    @FXML
    public Label tvHostType;
    @FXML
    private TextField txtServerName;
    @FXML
    private TextField txtPort;
    @FXML
    private CheckBox chkPrivateServer;
    @FXML
    private TextField txtPassword;
    @FXML
    private CheckBox chkMaxConnections;
    @FXML
    private Spinner spnMaxConnections;
    @FXML
    private AnchorPane root;


    ServerConf serverConf;


    public void onStartButtonClick() {
        if (txtServerName.getText().isEmpty()) {
            tvHostType.setText("Server name can't be empty");
        } else if (txtServerName.getText().contains(" ")) {
            tvHostType.setText("Server name can't contain spaces");
        } else {
            try {
                if (!txtPort.getText().isEmpty()) {
                    port = Integer.parseInt(txtPort.getText());
                } else {
                    port = DEFAULT_PORT;
                }

                if (port < 49152 || port > 65535) {
                    tvHostType.setText("Port isn't between 49152-65535");
                }

                serverConf = new ServerConf(txtServerName.getText(), port);

                if (chkPrivateServer.isSelected() && !txtPassword.getText().isEmpty()) {
                    serverConf.setPswd(txtPassword.getText());
                } else {
                    serverConf.setPswd("");
                }

                if (chkMaxConnections.isSelected()) {
                    serverConf.setMaxCon((int) spnMaxConnections.getValue());
                } else {
                    serverConf.setMaxCon(-1);
                }

                FXMLLoader fxmlHostChatLoader = new FXMLLoader(Main.class.getResource("view/host-chat.fxml"));
                Scene sceneChat = new Scene(fxmlHostChatLoader.load());
                Stage stage = (Stage) root.getScene().getWindow();

                stage.close();
                stage.setScene(sceneChat);
                stage.show();

                ((HostChatController) fxmlHostChatLoader.getController()).start(serverConf);
            } catch (IOException e) {
                System.out.println(e);
            } catch (NumberFormatException e) {
                System.out.println(e);
                tvHostType.setText("Port isn't a number");
            }
        }
    }

    public void onBackButtonClick() {
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

    public void onChkConnectionAction() {
        if (chkMaxConnections.isSelected()) {
            spnMaxConnections.setDisable(false);
        } else {
            spnMaxConnections.setDisable(true);
        }
    }

    public void onChkPrivateAction() {
        if (chkPrivateServer.isSelected()) {
            txtPassword.setDisable(false);
        } else {
            txtPassword.setDisable(true);
        }
    }
}
