package com.github.tpiskorski.vboxcm.domain;

public class Server {

    private   String address;

    public Server(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
