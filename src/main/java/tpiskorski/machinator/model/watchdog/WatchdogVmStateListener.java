package tpiskorski.machinator.model.watchdog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler;
import tpiskorski.machinator.model.vm.VirtualMachineState;

public class WatchdogVmStateListener implements ChangeListener<VirtualMachineState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogVmStateListener.class);

    private final WatchdogScheduler watchdogScheduler;
    private final Watchdog watchdog;

    public WatchdogVmStateListener(Watchdog watchdog, WatchdogScheduler watchdogScheduler) {
        this.watchdog = watchdog;
        this.watchdogScheduler = watchdogScheduler;
    }

    @Override
    public void changed(ObservableValue<? extends VirtualMachineState> observable, VirtualMachineState oldValue, VirtualMachineState newValue) {
        if (newValue == VirtualMachineState.POWEROFF) {
            LOGGER.debug("Notifying watchdog {}", watchdog);
            watchdogScheduler.schedule(watchdog);
        }
    }
}
