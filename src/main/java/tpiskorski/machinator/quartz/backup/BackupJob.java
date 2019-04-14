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

import java.io.IOException;

@Component
public class BackupJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private final ConfigService configService;
    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

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

        Command command = commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "backupFile", backupDefinition.getVm().getVmName());
        try {
            CommandResult result = localMachineCommandExecutor.executeIn(command, configService.getConfig().getBackupLocation());
            if (result.isFailed()) {
                LOGGER.error("Backup job failed");
                throw new JobExecutionException(result.getError());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
