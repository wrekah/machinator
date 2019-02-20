package com.github.tpiskorski.vboxcm.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class ServerRepository {

    private ObservableList<Server> observableList = FXCollections.observableArrayList();

    public void add(Server server) {
        observableList.add(server);
    }


    public ObservableList<Server>  getList() {
        return observableList;
    }

    public void remove() {
        observableList.remove(0);
    }
}
