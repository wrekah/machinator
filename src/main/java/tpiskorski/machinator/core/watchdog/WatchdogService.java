package tpiskorski.machinator.core.watchdog;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchdogService {

    private final WatchdogRepository watchdogRepository;

    @Autowired public WatchdogService(WatchdogRepository watchdogRepository) {
        this.watchdogRepository = watchdogRepository;
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
        watchdogToActivate.setActive(true);
    }

    public void deactivate(Watchdog watchdogToActivate) {
        watchdogToActivate.setActive(false);
    }
}
