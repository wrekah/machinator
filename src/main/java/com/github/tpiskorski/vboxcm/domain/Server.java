package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class Server {

    private StringProperty address = new SimpleStringProperty();

    public Server(String address) {
        this.address.set(address);
    }

    static Callback<Server, Observable[]> extractor() {
        return (Server server) -> new Observable[]{server.getAddress()};
    }

    public StringProperty getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);

    }

    @Override public int hashCode() {
        return Objects.hashCode(address.get().hashCode());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Server)) {
            return false;
        }
        Server that = (Server) obj;
        return Objects.equals(this.getAddress().get(), that.getAddress().get());

    }
}
