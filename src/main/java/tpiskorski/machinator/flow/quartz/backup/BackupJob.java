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
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.parser.ExportVmResultInterpreter;
import tpiskorski.machinator.flow.quartz.service.BackupService;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
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

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private final ConfigService configService;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();
    private ScpClient scpClient = new ScpClient();

    @Autowired private BackupService backupService;
    @Autowired private CleanupService cleanupService;

    @Autowired
    public BackupJob(ConfigService configService, CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.configService = configService;
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
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

        RemoteContext remoteContext = RemoteContext.of(backupDefinition.getServer());

        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(backupDefinition.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "~/" + backupName, backupDefinition.getVm().getVmName()))
            .build();

        commandExecutor.execute(exportVm);
        scpClient.copyRemoteToLocal(remoteContext, "~/", backupLocation.toString(), backupName + ".ova");

        cleanupService.cleanup(backupDefinition.getServer(), "~/" + backupName + ".ova");
    }

    private void doLocalBackup(BackupDefinition backupDefinition) throws JobExecutionException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + backupDefinition.getServer().getAddress() + "/" + backupDefinition.getVm().getVmName());
        String backupName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-HH:mm"));
        String backup = backupLocation + "/" + backupName;

        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(backupDefinition.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, backup, backupDefinition.getVm().getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(exportVm);
        if (!exportVmResultInterpreter.isSuccess(result)) {
            LOGGER.error("Backup job failed");
            throw new JobExecutionException(result.getError());
        }
    }
}
