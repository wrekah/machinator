package tpiskorski.machinator.model.watchdog;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler;
import tpiskorski.machinator.lifecycle.quartz.PersistScheduler;
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;
import tpiskorski.machinator.model.watchdog.listener.WatchdogListeners;

import java.util.HashMap;
import java.util.Map;

@Service
public class WatchdogService {

    private final WatchdogRepository watchdogRepository;
    private final WatchdogScheduler watchdogScheduler;
    private final PersistScheduler persistScheduler;

    private Map<Watchdog, WatchdogListeners> watchdogToListeners = new HashMap<>();

    @Autowired
    public WatchdogService(WatchdogRepository watchdogRepository, WatchdogScheduler watchdogScheduler, PersistScheduler persistScheduler) {
        this.watchdogRepository = watchdogRepository;
        this.watchdogScheduler = watchdogScheduler;
        this.persistScheduler = persistScheduler;
    }

    public void add(Watchdog watchdog) {
        watchdogRepository.add(watchdog);
        persistScheduler.schedulePersistence(PersistenceType.WATCHDOG);
    }

    public void put(Watchdog watchdog) {
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

            WatchdogListeners listeners = new WatchdogListeners(watchdogToActivate, watchdogScheduler);
            watchdogToListeners.put(watchdogToActivate, listeners);
            listeners.activate();
        }
    }

    public void deactivate(Watchdog watchdogToActivate) {
        if (watchdogToActivate.isActive()) {
            watchdogToActivate.setActive(false);
            WatchdogListeners listeners = watchdogToListeners.get(watchdogToActivate);
            listeners.deactivate();
        }
    }

    public boolean contains(Watchdog watchdog) {
        return watchdogRepository.contains(watchdog);
    }

    public void exhaust(Watchdog watchdog) {
        watchdog.setWatchdogServer(null);
    }
}
