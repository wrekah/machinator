package com.github.tpiskorski.vboxcm.stub.dynamic;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class AddServerTask extends Task<Void> {

    private ServerService serverService;
    private Server server;

    public AddServerTask(ServerService serverService, Server server) {
        this.serverService = serverService;
        this.server = server;
    }

    @Override
    protected Void call() throws Exception {
        Thread.sleep(5000);
        Platform.runLater(() -> serverService.add(server));
        return null;
    }
}
