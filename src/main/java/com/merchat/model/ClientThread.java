package com.merchat.model;

import com.merchat.controller.HostChatController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String uname = "";

    private Socket socket;
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

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            uname = input.readLine();

            try {
                String msg = "";
                server.broadcast(uname + " has just connected, say hi [" + (server.getClients().size() + 1) + " user connected as of right now]");

                while (!msg.equals("\\exit")) {
                    msg = input.readLine();
                    if (!msg.equals("\\exit")) {
                        server.broadcast(this, uname, msg);
                    }
                }
            } catch (IOException e) {

            } catch (NullPointerException e) {

            } finally {
                server.getClients().remove(this);
                server.broadcast("> User " + uname + " disconected [" + (server.getClients().size() + 1) + " user remaining]");
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }
        } catch (IOException e) {
            server.getClients().remove(this);
            try {
                socket.close();
            } catch (IOException f) {
                System.exit(0);
            }
        }

    }
}
