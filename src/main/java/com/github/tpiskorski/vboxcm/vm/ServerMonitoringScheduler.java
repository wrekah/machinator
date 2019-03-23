package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.server.ServerState;
import com.github.tpiskorski.vboxcm.core.server.ServerType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ServerMonitoringScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMonitoringScheduler.class);

    private final ServerService serverService;
    private final ServerMonitoringService serverMonitoringService;

    private LocalhostConnectivityChecker localhostConnectivityChecker = new LocalhostConnectivityChecker();

    @Autowired
    public ServerMonitoringScheduler(ServerService serverService, ServerMonitoringService serverMonitoringService) {
        this.serverService = serverService;
        this.serverMonitoringService = serverMonitoringService;
    }

    @Scheduled(fixedRate = 10000L)
    public void monitor() {
        ObservableList<Server> serversView = FXCollections.observableArrayList(serverService.getServers());

        for (Server server : serversView) {
            if (server.getServerType() == ServerType.LOCAL) {
                LOGGER.info("Scheduled localhost scan...");
                serverMonitoringService.scheduleScan(server);
            }
        }
    }
}
