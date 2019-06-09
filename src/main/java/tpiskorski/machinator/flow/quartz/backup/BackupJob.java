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
import tpiskorski.machinator.flow.quartz.service.*;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    @Autowired private BackupService backupService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;
    @Autowired private CopyService copyService;
    @Autowired private VmManipulator vmManipulator;
    @Autowired private VmInfoService vmInfoService;

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        LOGGER.info("Backup started for {}", backupDefinition.id());

        backupService.assertBackupCount(backupDefinition);

        VirtualMachine vm = backupDefinition.getVm();

        vm.lock();
        try {
            powerOffIfRunning(vm);
            doBackup(backupDefinition);
            vmManipulator.start(vm);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Backup completed for {}", backupDefinition.id());
    }

    private void powerOffIfRunning(VirtualMachine vm) {
        if (vmInfoService.state(vm) != VirtualMachineState.POWEROFF) {
            vmManipulator.powerOff(vm);
            vmInfoService.pollState(vm, VirtualMachineState.POWEROFF);
        }
    }

    private void doBackup(BackupDefinition backupDefinition) throws JobExecutionException {
        try {
            if (backupDefinition.getServer().getServerType() == ServerType.LOCAL) {
                doLocalBackup(backupDefinition);
            } else {
                doRemoteBackup(backupDefinition);
            }
        } catch (JSchException | IOException e) {
            throw new JobExecutionException(e);
        }
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

        String remoteTemporaryFilePath = backupService.getRemoteTemporaryFilePath(vm);

        try {
            cleanupService.cleanup(server, remoteTemporaryFilePath);
            exportVmService.exportVm(server, remoteTemporaryFilePath, vm.getVmName());

            String backupPath = backupService.getBackupPath(backupDefinition);
            LOGGER.info("Backup to be put into dir {}", backupPath);
            copyService.copyRemoteToLocal(server, remoteTemporaryFilePath, backupPath);
        } finally {
            cleanupService.cleanup(server, remoteTemporaryFilePath);
        }
    }
}
