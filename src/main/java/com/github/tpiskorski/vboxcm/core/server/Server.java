package com.github.tpiskorski.vboxcm.core.server;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class Server implements Comparable<Server> {

    private static final String LOCALHOST = "Local Machine";

    private StringProperty address = new SimpleStringProperty();
    private StringProperty port = new SimpleStringProperty();

    private ObjectProperty<ServerState> serverState = new SimpleObjectProperty<>();
    private ObjectProperty<ServerType> serverType = new SimpleObjectProperty<>();

    public Server(String address, String port) {
        setAddress(address);
        setPort(port);
        setServerState(ServerState.UNKNOWN);

        if (address.equals(LOCALHOST)) {
            setServerType(ServerType.LOCAL);
        } else {
            setServerType(ServerType.REMOTE);
        }
    }

    static Callback<Server, Observable[]> extractor() {
        return (Server server) -> new Observable[]{server.serverStateProperty()};
    }

    public String getSimpleAddress() {
        if (getServerType() == ServerType.LOCAL) {
            return getAddress();
        } else {
            return getAddress() + ":" + getPort();
        }
    }

    public ServerType getServerType() {
        return serverType.get();
    }

    public void setServerType(ServerType serverType) {
        this.serverType.set(serverType);
    }

    public ObjectProperty<ServerType> serverTypeProperty() {
        return serverType;
    }

    public String getPort() {
        return port.get();
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public StringProperty portProperty() {
        return port;
    }

    public ServerState getServerState() {
        return serverState.get();
    }

    public void setServerState(ServerState serverState) {
        this.serverState.set(serverState);
    }

    public ObjectProperty<ServerState> serverStateProperty() {
        return serverState;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    @Override public int hashCode() {
        return Objects.hash(getAddress(), getPort());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Server)) {
            return false;
        }
        Server that = (Server) obj;
        return Objects.equals(this.getAddress(), that.getAddress()) && Objects.equals(this.getPort(), that.getPort());
    }

    @Override public int compareTo(Server that) {
        if (this.getServerType() == ServerType.LOCAL) {
            return -1;
        }
        if (that.getServerType() == ServerType.LOCAL) {
            return 1;
        }
        return this.getAddress().compareTo(that.getAddress());
    }
}
