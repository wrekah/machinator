package com.github.tpiskorski.vboxcm.stub.dynamic;

import com.github.tpiskorski.vboxcm.core.server.Server;
import javafx.concurrent.Task;

public class CheckConnectivityTask extends Task<Void> {

    private final Server server;

    CheckConnectivityTask(Server server) {
        this.server = server;
    }

    @Override protected Void call() throws Exception {
        Thread.sleep(3000);
        return null;
    }
}
