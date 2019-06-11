package tpiskorski.machinator.lifecycle.state.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableWatchdog;
import tpiskorski.machinator.model.watchdog.WatchdogService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WatchdogStateManager extends StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogStateManager.class);

    private final WatchdogService watchdogService;

    @Autowired
    public WatchdogStateManager(WatchdogService watchdogService) {
        this.watchdogService = watchdogService;
    }

    @Override public String getPersistResourceFileName() {
        return "data/watchdogs.dat";
    }

    @Override public PersistenceType getPersistenceType() {
        return PersistenceType.WATCHDOG;
    }

    @Override public void persist() {
        LOGGER.info("Starting servers persistence");

        List<SerializableWatchdog> toSerialize = watchdogService.getWatchdogs().stream()
            .map(SerializableWatchdog::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist(getPersistResourceFileName(), toSerialize);
            LOGGER.info("Persisted watchdogs!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist watchdogs", ex);
        }
    }

    @Override public void restore() {
        LOGGER.info("Starting restoring watchdogs state");

        try {
            List<SerializableWatchdog> restoredWatchdogs = objectRestorer.restore(getPersistResourceFileName());

            LOGGER.info("Restoring {} watchdogs", restoredWatchdogs.size());

            restoredWatchdogs.stream()
                .map(SerializableWatchdog::toWatchdog)
                .collect(Collectors.toList())
                .forEach(watchdogService::put);

            LOGGER.info("Watchdogs state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore watchdogs state", ex);
        }
    }
}
