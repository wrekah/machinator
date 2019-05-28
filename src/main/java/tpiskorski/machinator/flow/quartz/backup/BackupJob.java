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
import tpiskorski.machinator.flow.quartz.service.BackupService;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
import tpiskorski.machinator.flow.quartz.service.CopyService;
import tpiskorski.machinator.flow.quartz.service.ExportVmService;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.io.IOException;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    @Autowired private BackupService backupService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;
    @Autowired private CopyService copyService;

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
        } catch (JSchException | IOException e) {
            throw new JobExecutionException(e);
        }

        LOGGER.info("Backup completed for {}", backupDefinition.id());
    }

    private void doLocalBackup(BackupDefinition backupDefinition) {
        String backupPath = backupService.getBackupPath(backupDefinition);

        LOGGER.info("Backup to be put into dir {}", backupPath);

        Server server = backupDefinition.getServer();
        VirtualMachine vm = backupDefinition.getVm();

        exportVmService.exportVm(server, backupPath, vm.getVmName());
    }

    private void doRemoteBackup(BackupDefinition backupDefinition) throws JSchException, IOException {
        Server server = backupDefinition.getServer();
        VirtualMachine vm = backupDefinition.getVm();

        String temporaryFilePath = backupService.getTemporaryFilePath(backupDefinition);
        exportVmService.exportVm(server, temporaryFilePath, vm.getVmName());

        String backupPath = backupService.getBackupPath(backupDefinition);
        LOGGER.info("Backup to be put into dir {}", backupPath);
        copyService.copyRemoteToLocal(server, temporaryFilePath, backupPath);

        cleanupService.cleanup(server, temporaryFilePath);
    }
}
