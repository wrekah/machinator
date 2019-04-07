package tpiskorski.vboxcm.quartz;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.core.server.ServerType;
import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.util.List;

@Component
public class ServerScanJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerScanJob.class);

    private ServerMonitoringService serverMonitoringService;
    private ServerService serverService;

    @Autowired
    public ServerScanJob(ServerMonitoringService serverMonitoringService, @Lazy ServerService serverService) {
        this.serverMonitoringService = serverMonitoringService;
        this.serverService = serverService;
    }

    @Override protected void executeInternal(JobExecutionContext jobExecutionContext) {
        LOGGER.info("Servers scan started...");
        try {
            ObservableList<Server> serversView = FXCollections.observableArrayList(serverService.getServers());
            for (Server server : serversView) {
                if (server.getServerType() == ServerType.LOCAL) {
                    List<VirtualMachine> vms = serverMonitoringService.monitor(server);
                    Platform.runLater(() -> serverService.upsert(server, vms));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Server scan error", e);
        }
        LOGGER.info("Servers scan finished...");
    }
}
