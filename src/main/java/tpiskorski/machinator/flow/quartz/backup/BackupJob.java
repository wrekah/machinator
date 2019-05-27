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
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.quartz.service.BackupService;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
import tpiskorski.machinator.flow.quartz.service.ExportVmService;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.server.ServerType;

import java.io.IOException;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private ScpClient scpClient = new ScpClient();

    @Autowired private BackupService backupService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        LOGGER.info("Backup started for {}", backupDefinition.id());

        backupService.assertBackupCount(backupDefinition);

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

    private void doRemoteBackup(BackupDefinition backupDefinition) throws JobExecutionException, JSchException, IOException, InterruptedException {
        String backupLocation = backupService.getBackupLocation(backupDefinition).toString();
        String backupName = backupService.getNextBackupName(backupDefinition);

        exportVmService.exportVm(backupDefinition.getServer(), "~/" + backupName, backupDefinition.getVm().getVmName());

        RemoteContext remoteContext = RemoteContext.of(backupDefinition.getServer());
        scpClient.copyRemoteToLocal(remoteContext, "~/", backupLocation, backupName + ".ova");

        cleanupService.cleanup(backupDefinition.getServer(), "~/" + backupName + ".ova");
    }

    private void doLocalBackup(BackupDefinition backupDefinition) throws JobExecutionException {
        String backupPath = backupService.getBackupPath(backupDefinition);

        exportVmService.exportVm(backupDefinition.getServer(), backupPath, backupDefinition.getVm().getVmName());
    }
}
