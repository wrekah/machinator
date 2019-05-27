package tpiskorski.machinator.flow.quartz.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.quartz.service.VmLister;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.ui.core.PlatformThreadAction;
import tpiskorski.machinator.ui.core.PlatformThreadUpdater;

import java.util.List;

@DisallowConcurrentExecution
@Component
public class ServerRefreshJob extends QuartzJobBean {

    static final String NAME = "ServerRefreshJob";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRefreshJob.class);

    @Autowired private VmLister vmLister;
    @Autowired private PlatformThreadUpdater platformThreadUpdater;

    private final ServerService serverService;

    @Autowired
    public ServerRefreshJob(@Lazy ServerService serverService) {
        this.serverService = serverService;
    }

    @Override protected void executeInternal(JobExecutionContext jobExecutionContext) {
        LOGGER.debug("Servers refresh started...");
        try {
            ObservableList<Server> serversView = FXCollections.observableArrayList(serverService.getServers());

            for (Server server : serversView) {
                LOGGER.debug("Server refresh {}", server.getAddress());
                List<VirtualMachine> vms = vmLister.list(server);
                platformThreadUpdater.runLater(refresh(server, vms));
            }
        } catch (Exception e) {
            LOGGER.error("Server refresh error", e);
        }
        LOGGER.debug("Servers refresh finished...");
    }

    private PlatformThreadAction refresh(Server server, List<VirtualMachine> vms) {
        return () -> serverService.refresh(server, vms);
    }
}
