package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;

public class MonitorJob {

    private Server server;

    public MonitorJob(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
