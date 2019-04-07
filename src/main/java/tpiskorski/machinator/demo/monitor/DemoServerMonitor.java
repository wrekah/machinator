package tpiskorski.machinator.demo.monitor;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerService;
import tpiskorski.machinator.core.server.ServerType;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.quartz.monitor.ServerMonitor;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Profile("demo")
@Component
public class DemoServerMonitor implements ServerMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServerMonitor.class);

    private final ServerService serverService;
    private final StubSshClient stubSshClient = new StubSshClient();

    private AtomicBoolean isFreezed = new AtomicBoolean(false);

    @Autowired public DemoServerMonitor(ServerService serverService) {
        this.serverService = serverService;
    }

    @Scheduled(fixedRate = 10000L)
    public void monitor() {
        LOGGER.info("Servers scan started...");
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

        LOGGER.info("Servers scan finished...");
    }

    @Override public void pause() {
        isFreezed.set(!isFreezed.get());
    }

    @Override public boolean isPaused() {
        return isFreezed.get();
    }
}

