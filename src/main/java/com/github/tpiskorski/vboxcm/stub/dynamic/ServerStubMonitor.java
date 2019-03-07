package com.github.tpiskorski.vboxcm.stub.dynamic;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Profile("stub_dynamic")
@Component
public class ServerStubMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStubMonitor.class);
    private final ServerService serverService;

    private ThreadLocalRandom current = ThreadLocalRandom.current();

    @Autowired public ServerStubMonitor(ServerService serverService) {
        this.serverService = serverService;
    }

    @Scheduled(fixedRate = 10000L)
    public void monitor() {
        LOGGER.info("About to monitor...");
        ObservableList<Server> servers = serverService.getServers();

        if (servers.isEmpty()) {
            LOGGER.info("Nothing to monitor");
            return;
        }

        for (Server server : servers) {
            randomStatusUpdate(server);
        }

        LOGGER.info("Finished monitor cycle");
    }

    private void randomStatusUpdate(Server server) {
        if (current.nextBoolean()) {
            Platform.runLater(() -> serverService.updateReachable(server));
        } else {
            Platform.runLater(() -> serverService.updateUnreachable(server));
        }
    }
}

