package com.github.tpiskorski.vboxcm.domain;

public class Server {

    private final String address;

    public Server(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
