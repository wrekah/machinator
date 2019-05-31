package tpiskorski.machinator.model.watchdog.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler;
import tpiskorski.machinator.model.server.ServerState;
import tpiskorski.machinator.model.watchdog.Watchdog;

public class WatchdogServerStateListener implements ChangeListener<ServerState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogServerStateListener.class);

    private final WatchdogScheduler watchdogScheduler;
    private final Watchdog watchdog;

    public WatchdogServerStateListener(Watchdog watchdog, WatchdogScheduler watchdogScheduler) {
        this.watchdog = watchdog;
        this.watchdogScheduler = watchdogScheduler;
    }

    @Override
    public void changed(ObservableValue<? extends ServerState> observable, ServerState oldValue, ServerState newValue) {
        if (newValue == ServerState.NOT_REACHABLE && oldValue != ServerState.REACHABLE) {
            LOGGER.debug("Notifying watchdog because server is down {}", watchdog);
            watchdogScheduler.schedule(watchdog);
        }
    }
}
