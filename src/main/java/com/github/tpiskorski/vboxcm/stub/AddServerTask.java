package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.domain.Server;
import javafx.concurrent.Task;

public class AddServerTask extends Task<Void> {

    private Server server;

    public AddServerTask(Server server) {
        this.server = server;
    }

    @Override
    protected Void call() throws Exception {
        Thread.sleep(5000);
        return null;
    }
}
