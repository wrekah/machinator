package tpiskorski.machinator.model.watchdog.listener;

import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler;
import tpiskorski.machinator.model.watchdog.Watchdog;

public class WatchdogListeners {

    private final Watchdog watchdogToActivate;

    private WatchdogVmStateListener watchdogVmStateListener;
    private WatchdogServerStateListener watchdogServerStateListener;

    public WatchdogListeners(Watchdog watchdogToActivate, WatchdogScheduler watchdogScheduler) {
        this.watchdogToActivate = watchdogToActivate;

        watchdogVmStateListener = new WatchdogVmStateListener(watchdogToActivate, watchdogScheduler);
        watchdogServerStateListener = new WatchdogServerStateListener(watchdogToActivate, watchdogScheduler);
    }

    public void activate() {
        watchdogToActivate.getVirtualMachine().stateProperty().addListener(watchdogVmStateListener);
        watchdogToActivate.getVirtualMachine().getServer().serverStateProperty().addListener(watchdogServerStateListener);
    }

    public void deactivate() {
        watchdogToActivate.getVirtualMachine().stateProperty().removeListener(watchdogVmStateListener);
        watchdogToActivate.getVirtualMachine().getServer().serverStateProperty().removeListener(watchdogServerStateListener);
    }
}
