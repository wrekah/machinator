package tpiskorski.machinator.lifecycle.state.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableBackupDefinition;
import tpiskorski.machinator.model.backup.BackupDefinitionService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BackupStateManager extends StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupStateManager.class);

    private final BackupDefinitionService backupDefinitionService;

    @Autowired
    public BackupStateManager(BackupDefinitionService backupDefinitionService) {
        this.backupDefinitionService = backupDefinitionService;
    }

    @Override public String getPersistResourceFileName() {
        return "data/backups.dat";
    }

    @Override public PersistenceType getPersistenceType() {
        return PersistenceType.BACKUP_DEFINITION;
    }

    @Override public void persist() {
        LOGGER.info("Starting backups persistence");

        List<SerializableBackupDefinition> toSerialize = backupDefinitionService.getBackups().stream()
            .map(SerializableBackupDefinition::new)
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
            List<SerializableBackupDefinition> restoredBackups = objectRestorer.restore(getPersistResourceFileName());

            LOGGER.info("Restoring {} backups", restoredBackups.size());

            restoredBackups.stream()
                .map(SerializableBackupDefinition::toBackup)
                .collect(Collectors.toList())
                .forEach(backupDefinitionService::add);

            LOGGER.info("Backups state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore backups state", ex);
        }
    }
}
