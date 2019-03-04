package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.server.Server;
import com.github.tpiskorski.vboxcm.server.ServerRepository;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class AddServerTask extends Task<Void> {

    private ServerRepository serverRepository;
    private Server server;

    public AddServerTask(ServerRepository serverRepository, Server server) {
        this.serverRepository = serverRepository;
        this.server = server;
    }

    @Override
    protected Void call() throws Exception {
        Thread.sleep(5000);
        Platform.runLater(() -> serverRepository.add(server));
        return null;
    }
}
