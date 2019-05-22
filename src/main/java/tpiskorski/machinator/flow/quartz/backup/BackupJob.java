package tpiskorski.machinator.flow.quartz.backup;

import com.jcraft.jsch.JSchException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.quartz.service.BackupService;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
import tpiskorski.machinator.flow.quartz.service.ExportVmService;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.server.ServerType;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private final ConfigService configService;

    private ScpClient scpClient = new ScpClient();

    @Autowired private BackupService backupService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;

    @Autowired
    public BackupJob(ConfigService configService) {
        this.configService = configService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        LOGGER.info("Backup started for {}", backupDefinition.id());

        assertBackupLimit(backupDefinition);

        try {
            if (backupDefinition.getServer().getServerType() == ServerType.LOCAL) {
                doLocalBackup(backupDefinition);
            } else {
                doRemoteBackup(backupDefinition);
            }
        } catch (JSchException | IOException | InterruptedException e) {
            throw new JobExecutionException(e);
        }

        LOGGER.info("Backup completed for {}", backupDefinition.id());
    }

    private void assertBackupLimit(BackupDefinition backupDefinition) {
        long count = backupService.getBackupCount(backupDefinition);

        if (count >= backupDefinition.getFileLimit()) {
            LOGGER.error("Backup job failed. File limit exceeded");
            throw new ExecutionException("File limit exceeded");
        }
    }

    private void doRemoteBackup(BackupDefinition backupDefinition) throws JobExecutionException, JSchException, IOException, InterruptedException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + backupDefinition.getServer().getAddress() + "/" + backupDefinition.getVm().getVmName());
        String backupName = "backup-" + LocalDateTime.now();

        exportVmService.exportVm(backupDefinition.getServer(), "~/" + backupName, backupDefinition.getVm().getVmName());

        RemoteContext remoteContext = RemoteContext.of(backupDefinition.getServer());
        scpClient.copyRemoteToLocal(remoteContext, "~/", backupLocation.toString(), backupName + ".ova");

        cleanupService.cleanup(backupDefinition.getServer(), "~/" + backupName + ".ova");
    }

    private void doLocalBackup(BackupDefinition backupDefinition) throws JobExecutionException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + backupDefinition.getServer().getAddress() + "/" + backupDefinition.getVm().getVmName());
        String backupName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-HH:mm"));
        String backup = backupLocation + "/" + backupName;

        exportVmService.exportVm(backupDefinition.getServer(), backup, backupDefinition.getVm().getVmName());
    }
}
