package com.github.tpiskorski.vboxcm.stub.dynamic;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.server.ServerState;
import com.github.tpiskorski.vboxcm.core.server.ServerType;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.stub.net.SshException;
import com.github.tpiskorski.vboxcm.stub.net.StubSshClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Profile("stub_dynamic")
@Component
public class ServerStubMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStubMonitor.class);

    private final ServerService serverService;
    private final StubSshClient stubSshClient;
    private AtomicBoolean isFreezed = new AtomicBoolean(false);

    @Autowired public ServerStubMonitor(ServerService serverService, StubSshClient stubSshClient) {
        this.serverService = serverService;
        this.stubSshClient = stubSshClient;
    }

    public AtomicBoolean getIsFreezed() {
        return isFreezed;
    }

    @Scheduled(fixedRate = 10000L)
    public void monitor() {
        LOGGER.info("About to monitor...");
        ObservableList<Server> servers = serverService.getServers();

        if (servers.isEmpty() || isFreezed.get()) {
            LOGGER.info("Nothing to monitor");
            return;
        }

        for (Server server : servers) {
            if (server.getServerType() == ServerType.LOCAL) {
                return;
            }

            try {
                List<VirtualMachine> vms = stubSshClient.getVms(server);
                Platform.runLater(() -> serverService.updateReachable(server, vms));
            } catch (SshException sshException) {
                Platform.runLater(() -> serverService.updateUnreachable(server));
            }
        }

        LOGGER.info("Finished monitor cycle");
    }

    public void freeze() {
        isFreezed.set(!isFreezed.get());
    }
}

