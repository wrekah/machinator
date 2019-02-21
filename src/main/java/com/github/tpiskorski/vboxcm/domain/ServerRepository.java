package com.github.tpiskorski.vboxcm.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class ServerRepository {

    private ObservableList<Server> serverObservableList = FXCollections.observableArrayList(Server.extractor());

    public void add(Server server) {
        serverObservableList.add(server);
    }

    public ObservableList<Server> getServersList() {
        return serverObservableList;
    }

    public void remove(Server server) {
        serverObservableList.remove(server);
    }
}
