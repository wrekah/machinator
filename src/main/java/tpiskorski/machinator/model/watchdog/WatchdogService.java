package tpiskorski.machinator.model.watchdog;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler;

import java.util.HashMap;
import java.util.Map;

@Service
public class WatchdogService {

    private final WatchdogRepository watchdogRepository;
    private final WatchdogScheduler watchdogScheduler;

    private Map<Watchdog, WatchdogVmStateListener> watchdogToListener = new HashMap<>();

    @Autowired public WatchdogService(WatchdogRepository watchdogRepository, WatchdogScheduler watchdogScheduler) {
        this.watchdogRepository = watchdogRepository;
        this.watchdogScheduler = watchdogScheduler;
    }

    public void add(Watchdog watchdog) {
        watchdogRepository.add(watchdog);
    }

    public ObservableList<Watchdog> getWatchdogs() {
        return watchdogRepository.getWatchdogs();
    }

    public void remove(Watchdog watchdog) {
        watchdogRepository.remove(watchdog);
    }

    public void activate(Watchdog watchdogToActivate) {
        if (!watchdogToActivate.isActive()) {
            watchdogToActivate.setActive(true);

            WatchdogVmStateListener listener = new WatchdogVmStateListener(watchdogToActivate, watchdogScheduler);

            watchdogToActivate.getVirtualMachine().stateProperty().addListener(listener);
            watchdogToListener.put(watchdogToActivate, listener);
        }
    }

    public void deactivate(Watchdog watchdogToActivate) {
        if (watchdogToActivate.isActive()) {
            watchdogToActivate.setActive(false);
            WatchdogVmStateListener listener = watchdogToListener.get(watchdogToActivate);
            watchdogToActivate.getVirtualMachine().stateProperty().removeListener(listener);
        }
    }

    public boolean contains(Watchdog watchdog) {
        return watchdogRepository.contains(watchdog);
    }
}
