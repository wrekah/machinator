package com.github.tpiskorski.vboxcm.core.server;

import com.github.tpiskorski.vboxcm.vm.ServerMonitoringScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerCoordinatingService {

    private final ServerService serverService;
    private final ServerMonitoringScheduler serverMonitoringScheduler;

    @Autowired
    public ServerCoordinatingService(ServerService serverService, ServerMonitoringScheduler serverMonitoringScheduler) {
        this.serverService = serverService;
        this.serverMonitoringScheduler = serverMonitoringScheduler;
    }

    public void add(Server server) {
        serverService.add(server);
        serverMonitoringScheduler.scheduleScan(server);
    }
}
