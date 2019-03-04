package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerTaskFactory {

    private final ServerService serverService;

    @Autowired public AddServerTaskFactory(ServerService serverService) {
        this.serverService = serverService;
    }

    public AddServerTask taskFor(Server server) {
        return new AddServerTask(serverService, server);
    }
}
