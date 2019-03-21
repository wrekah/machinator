package com.github.tpiskorski.vboxcm.core.server;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import java.util.Objects;

public class Server implements Comparable<Server> {

    private static final String LOCALHOST = "localhost";

    private StringProperty address = new SimpleStringProperty();
    private BooleanProperty reachable = new SimpleBooleanProperty();
    private ObjectProperty<ServerState> serverState = new SimpleObjectProperty<>();

    public Server(String address) {
        setAddress(address);
        if (address.startsWith(LOCALHOST)) {
            setServerState(ServerState.LOCALHOST);
        } else {
            setServerState(ServerState.UNKNOWN);
        }
    }

    static Callback<Server, Observable[]> extractor() {
        return (Server server) -> new Observable[]{server.addressProperty(), server.serverStateProperty()};
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

    public boolean isReachable() {
        return reachable.get();
    }

    public void setReachable(boolean reachable) {
        this.reachable.set(reachable);
    }

    public BooleanProperty reachableProperty() {
        return reachable;
    }

    @Override public int hashCode() {
        return Objects.hashCode(getAddress());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Server)) {
            return false;
        }
        Server that = (Server) obj;
        return Objects.equals(this.getAddress(), that.getAddress());
    }

    @Override public int compareTo(Server that) {
        if (this.getServerState() == ServerState.LOCALHOST) {
            return -1;
        }
        if (that.getServerState() == ServerState.LOCALHOST) {
            return 1;
        }
        return this.getAddress().compareTo(that.getAddress());
    }
}
