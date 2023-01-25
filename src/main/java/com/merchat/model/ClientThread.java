package com.merchat.model;

import com.merchat.controller.HostChatController;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private String uname = "";
    private boolean onLoop;

    private Socket socket;
    private Label label;
    private HostChatController server;
    private BufferedReader input;
    private PrintWriter output;

    public ClientThread(HostChatController server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }


    public PrintWriter getOutput() {
        return output;
    }

    public String getUname() {
        return uname;
    }

    public void setOnLoop(boolean onLoop) {
        this.onLoop = onLoop;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public void run() {
        try {
            boolean correctPswd = false;

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            uname = input.readLine();
            output.println(server.getServerConf().getServerName());
            if (server.getClients().size() < server.getServerConf().getMaxCon() || server.getServerConf().getMaxCon() == -1) {
                output.println("avaliableSpacesLeft");

                if (!server.getServerConf().getPswd().equals("")) {
                    output.println("serverProtectedByPassword");
                    correctPswd = server.getServerConf().getPswd().equals(input.readLine());
                    if (!correctPswd) {
                        close();
                    }
                } else {
                    output.println("serverNotProtectedByPassword");
                    correctPswd = true;
                }

                if (correctPswd) {
                    try {
                        String msg = "";
                        server.broadcast(uname + " has just connected, say hi [" + (server.getClients().size() + 1) + " user connected as of right now]");
                        onLoop = true;

                        while (onLoop) {
                            msg = input.readLine();
                            if (msg.equals("\\exit")) {
                                onLoop = false;
                            } else {
                                server.broadcast(this, uname, msg);
                            }
                        }
                    } catch (IOException | NullPointerException e) {
                        System.out.println(e);
                    } finally {
                        close();
                        server.broadcast("> User " + uname + " disconected [" + (server.getClients().size() + 1) + " user remaining]");
                    }
                }
            } else {
                output.println("noAvaliableSpacesLeft");
                close();
            }
        } catch (IOException e) {
            server.getClients().remove(this);
            try {
                socket.close();
            } catch (IOException f) {
                System.out.println(e);
            }
        }
    }

    public void close() {
        Platform.runLater(() -> server.getPaneUsers().getChildren().remove(label));
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        server.getClients().remove(this);
        onLoop = false;
    }
}
