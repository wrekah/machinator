package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.backup.BackupDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupService.class);

    private final ConfigService configService;

    @Autowired
    public BackupService(ConfigService configService) {
        this.configService = configService;
    }

    public long getBackupCount(BackupDefinition backupDefinition) {
        try {
            File location = getBackupLocation(backupDefinition);
            location.mkdirs();

            return Files.list(location.toPath()).count();
        } catch (IOException e) {
            LOGGER.error("Backup job failed", e);
            throw new ExecutionException(e);
        }
    }

    public void assertBackupCount(BackupDefinition backupDefinition) {
        long count = getBackupCount(backupDefinition);

        if (count >= backupDefinition.getFileLimit()) {
            LOGGER.error("Backup job failed. File limit exceeded");
            throw new ExecutionException("File limit exceeded");
        }
    }

    public File getBackupLocation(BackupDefinition backupDefinition) {
        return new File(configService.getConfig().getBackupLocation() + "/"
            + backupDefinition.getServer().getAddress() + "/"
            + backupDefinition.getVm().getVmName());
    }

    public String getBackupPath(BackupDefinition backupDefinition) {
        return getBackupLocation(backupDefinition) + "/" + getBackupName();
    }

    public String getBackupName() {
        return "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-HH:mm"));
    }
}
