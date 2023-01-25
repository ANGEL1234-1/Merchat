package com.merchat.model;

public class ServerConf {
    private String serverName;
    private int port;
    private String pswd = "";
    private int maxCon;


    public ServerConf(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }


    public String getServerName() {
        return serverName;
    }

    public int getPort() {
        return port;
    }

    public String getPswd() {
        return pswd;
    }

    public int getMaxCon() {
        return maxCon;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public void setMaxCon(int maxCon) {
        this.maxCon = maxCon;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
