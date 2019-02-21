package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
}
