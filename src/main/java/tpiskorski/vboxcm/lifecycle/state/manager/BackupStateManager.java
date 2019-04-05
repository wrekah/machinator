package tpiskorski.vboxcm.lifecycle.state.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.backup.BackupService;
import tpiskorski.vboxcm.lifecycle.state.serialize.model.SerializableBackup;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BackupStateManager extends StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupStateManager.class);

    private final BackupService backupService;

    @Autowired
    public BackupStateManager(BackupService backupService) {
        this.backupService = backupService;
    }

    @Override public String getPersistResourceFileName() {
        return "data/backups.dat";
    }

    @Override public void persist() {
        LOGGER.info("Starting backups persistence");

        List<SerializableBackup> toSerialize = backupService.getBackups().stream()
            .map(SerializableBackup::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist(getPersistResourceFileName(), toSerialize);
            LOGGER.info("Persisted backups!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist backups", ex);
        }
    }

    @Override public void restore() {
        LOGGER.info("Starting restoring backups state");

        try {
            List<SerializableBackup> restoredBackups = objectRestorer.restore(SerializableBackup.class, getPersistResourceFileName());

            LOGGER.info("Restoring {} backups", restoredBackups.size());

            restoredBackups.stream()
                .map(SerializableBackup::toBackup)
                .collect(Collectors.toList())
                .forEach(backupService::add);

            LOGGER.info("Backups state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore backups state", ex);
        }
    }
}
