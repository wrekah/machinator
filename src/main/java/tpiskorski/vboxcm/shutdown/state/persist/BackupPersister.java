package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.backup.BackupService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BackupPersister extends Persister {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupPersister.class);

    private final BackupService backupService;

    @Autowired
    public BackupPersister(BackupService backupService) {
        this.backupService = backupService;
    }

    @Override public void persist() {
        LOGGER.info("Starting backups persistence");

        List<SerializableBackup> toSerialize = backupService.getBackups().stream()
            .map(SerializableBackup::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist("servers.dat", toSerialize);
            LOGGER.info("Persisted backups!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist backups", ex);
        }
    }
}
