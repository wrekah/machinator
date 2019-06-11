package tpiskorski.machinator.lifecycle.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.state.manager.BackupStateManager;
import tpiskorski.machinator.lifecycle.state.manager.ServerStateManager;
import tpiskorski.machinator.lifecycle.state.manager.VirtualMachineStateManager;
import tpiskorski.machinator.lifecycle.state.manager.WatchdogStateManager;

@Profile("!dev")
@Service
public class AppStateRestorer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStateRestorer.class);

    private final ServerStateManager serverStateManager;
    private final VirtualMachineStateManager virtualMachineStateManager;
    private final BackupStateManager backupStateManager;
    private final WatchdogStateManager watchdogStateManager;

    @Autowired
    public AppStateRestorer(ServerStateManager serverStateManager, VirtualMachineStateManager virtualMachineStateManager, BackupStateManager backupStateManager, WatchdogStateManager watchdogStateManager) {
        this.serverStateManager = serverStateManager;
        this.virtualMachineStateManager = virtualMachineStateManager;
        this.backupStateManager = backupStateManager;
        this.watchdogStateManager = watchdogStateManager;
    }

    @Override public void afterPropertiesSet() {
        LOGGER.info("Started restoring app state...");

        serverStateManager.restore();
        virtualMachineStateManager.restore();
        backupStateManager.restore();
        watchdogStateManager.restore();

        LOGGER.info("App state restored");
    }
}
