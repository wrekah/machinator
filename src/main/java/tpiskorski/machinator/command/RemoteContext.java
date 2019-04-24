package tpiskorski.machinator.command;

import tpiskorski.machinator.core.server.Server;

public class RemoteContext {

    private String address;
    private int port;
    private String user;
    private String password;

    public static RemoteContext of(Server server) {
        RemoteContext remoteContext = new RemoteContext();
        remoteContext.setAddress(server.getAddress());
        remoteContext.setPort(Integer.parseInt(server.getPort()));
        remoteContext.setUser(server.getCredentials().getUser());
        remoteContext.setPassword(server.getCredentials().getPassword());
        return remoteContext;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
