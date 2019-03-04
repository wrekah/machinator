package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.server.Server;
import com.github.tpiskorski.vboxcm.server.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerTaskFactory {

    private final ServerRepository serverRepository;

    @Autowired public AddServerTaskFactory(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public AddServerTask taskFor(Server server) {
        return new AddServerTask(serverRepository, server);
    }
}
