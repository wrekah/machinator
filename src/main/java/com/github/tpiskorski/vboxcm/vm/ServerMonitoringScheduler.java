package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.server.ServerType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ServerMonitoringScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMonitoringScheduler.class);

    private final ServerService serverService;
    private final ServerMonitoringDaemon serverMonitoringDaemon;

    @Autowired
    public ServerMonitoringScheduler(@Lazy ServerService serverService, ServerMonitoringDaemon serverMonitoringDaemon) {
        this.serverService = serverService;
        this.serverMonitoringDaemon = serverMonitoringDaemon;
    }

    @Scheduled(fixedRate = 10000L)
    public void scheduleRegularScans() {
        ObservableList<Server> serversView = FXCollections.observableArrayList(serverService.getServers());

        for (Server server : serversView) {
            if (server.getServerType() == ServerType.LOCAL) {
                serverMonitoringDaemon.scheduleScan(server);
                LOGGER.info("Scheduled localhost scan...");
            }
        }
    }

    public void scheduleScan(Server server) {
        serverMonitoringDaemon.scheduleScan(server);
        LOGGER.info("Scheduled server scan {}", server);
    }
}
