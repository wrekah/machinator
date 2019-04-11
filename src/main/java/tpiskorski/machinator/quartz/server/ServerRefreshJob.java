package tpiskorski.machinator.quartz.server;

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
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerService;
import tpiskorski.machinator.core.server.ServerType;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.util.List;

@DisallowConcurrentExecution
@Component
public class ServerRefreshJob extends QuartzJobBean {

    static final String NAME = "ServerRefreshJob";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRefreshJob.class);

    private ServerRefreshService serverRefreshService;
    private ServerService serverService;

    @Autowired
    public ServerRefreshJob(ServerRefreshService serverRefreshService, @Lazy ServerService serverService) {
        this.serverRefreshService = serverRefreshService;
        this.serverService = serverService;
    }

    @Override protected void executeInternal(JobExecutionContext jobExecutionContext) {
        LOGGER.info("Servers refresh started...");
        try {
            ObservableList<Server> serversView = FXCollections.observableArrayList(serverService.getServers());
            for (Server server : serversView) {
                if (server.getServerType() == ServerType.LOCAL) {
                    List<VirtualMachine> vms = serverRefreshService.monitor(server);
                    serverService.upsert(server, vms);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Server refresh error", e);
        }
        LOGGER.info("Servers refresh finished...");
    }
}
