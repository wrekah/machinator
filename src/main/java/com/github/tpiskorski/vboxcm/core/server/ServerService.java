package com.github.tpiskorski.vboxcm.core.server;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    @Autowired public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public void remove(Server serverToRemove) {
        serverRepository.remove(serverToRemove);
    }

    public ObservableList<Server> getServers() {
        return serverRepository.getServersList();
    }

    public void add(Server server) {
        serverRepository.add(server);
    }
}
