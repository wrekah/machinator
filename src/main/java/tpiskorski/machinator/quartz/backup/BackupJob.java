package tpiskorski.machinator.quartz.backup;

import com.jcraft.jsch.JSchException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.action.CommandExecutor;
import tpiskorski.machinator.action.ExecutionContext;
import tpiskorski.machinator.command.BaseCommand;
import tpiskorski.machinator.command.CommandFactory;
import tpiskorski.machinator.command.CommandResult;
import tpiskorski.machinator.command.RemoteContext;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.core.backup.BackupDefinition;
import tpiskorski.machinator.core.server.ServerType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private final ConfigService configService;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();
    private ScpClient scpClient = new ScpClient();

    @Autowired
    public BackupJob(ConfigService configService, CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.configService = configService;
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        LOGGER.info("Started for {}", backupDefinition.id());

        try {
            if (backupDefinition.getServer().getServerType() == ServerType.LOCAL) {
                doLocalBackup(backupDefinition);
            } else {
                doRemoteBackup(backupDefinition);
            }
        } catch (JSchException | IOException | InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }

    private void doRemoteBackup(BackupDefinition backupDefinition) throws JobExecutionException, JSchException, IOException, InterruptedException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + backupDefinition.getServer().getAddress() + "/" + backupDefinition.getVm().getVmName());
        backupLocation.mkdirs();

        long count;
        try {
            count = Files.list(backupLocation.toPath()).count();
            if (count >= backupDefinition.getFileLimit()) {
                LOGGER.error("Backup job failed. File limit exceeded");
                throw new JobExecutionException("File limit exceeded");
            }
        } catch (IOException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        }

        RemoteContext remoteContext = RemoteContext.of(backupDefinition.getServer());

        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(backupDefinition.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "backupFile" + (count + 1), backupDefinition.getVm().getVmName()))
            .build();

        commandExecutor.execute(exportVm);
        scpClient.copyRemoteToLocal(remoteContext, "/home/piskorski", "/Users/sg0221154/machinator", "backupFile1.ova");
    }

    private void doLocalBackup(BackupDefinition backupDefinition) throws JobExecutionException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + backupDefinition.getServer().getAddress() + "/" + backupDefinition.getVm().getVmName());
        backupLocation.mkdirs();

        long count;
        try {
            count = Files.list(backupLocation.toPath()).count();
            if (count >= backupDefinition.getFileLimit()) {
                LOGGER.error("Backup job failed. File limit exceeded");
                throw new JobExecutionException("File limit exceeded");
            }
        } catch (IOException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        }

        try {
            ExecutionContext exportVm = ExecutionContext.builder()
                .executeOn(backupDefinition.getServer())
                .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "backupFile" + (count + 1), backupDefinition.getVm().getVmName()))
                .build();

            CommandResult result = commandExecutor.execute(exportVm);
            if (!exportVmResultInterpreter.isSuccess(result)) {
                LOGGER.error("Backup job failed");
                throw new JobExecutionException(result.getError());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
