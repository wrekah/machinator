package com.github.tpiskorski.vboxcm.core.server;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class Server {

    private StringProperty address = new SimpleStringProperty();
    private BooleanProperty reachable = new SimpleBooleanProperty();

    public ServerState getServerState() {
        return serverState;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    private ServerState serverState = ServerState.UNKNOWN;

    public Server(String address) {
        this.address.set(address);
    }

    static Callback<Server, Observable[]> extractor() {
        return (Server server) -> new Observable[]{server.addressProperty(), server.reachableProperty()};
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
}
