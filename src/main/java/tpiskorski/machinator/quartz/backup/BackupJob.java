package tpiskorski.machinator.quartz.backup;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.core.backup.BackupDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private final ConfigService configService;
    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();

    @Autowired
    public BackupJob(ConfigService configService, LocalMachineCommandExecutor localMachineCommandExecutor, CommandFactory commandFactory) {
        this.configService = configService;
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        LOGGER.info("Started for {}", backupDefinition.id());

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

        Command command = commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "backupFile" + (count + 1), backupDefinition.getVm().getVmName());
        try {
            CommandResult result = localMachineCommandExecutor.executeIn(command, backupLocation.toPath().toAbsolutePath().toString());
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
