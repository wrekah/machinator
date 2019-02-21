package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Server {

    private StringProperty address = new SimpleStringProperty();

    public Server(String address) {
        this.address.set(address);
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
