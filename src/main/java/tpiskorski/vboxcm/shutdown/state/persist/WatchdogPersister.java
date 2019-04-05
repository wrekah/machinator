package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.watchdog.WatchdogService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WatchdogPersister extends Persister {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogPersister.class);

    private final WatchdogService watchdogService;

    @Autowired
    public WatchdogPersister(WatchdogService watchdogService) {
        this.watchdogService = watchdogService;
    }

    @Override public String getPersistResourceFileName() {
        return "data/watchdogs.dat";
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
            objectRestorer.restore(SerializableWatchdog.class, getPersistResourceFileName()).stream()
                .map(SerializableWatchdog::toWatchdog)
                .collect(Collectors.toList())
                .forEach(watchdogService::add);

            LOGGER.info("Watchdogs state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore watchdogs state", ex);
        }
    }
}
