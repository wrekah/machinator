package tpiskorski.machinator.quartz.backup;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.command.CommandFactory;
import tpiskorski.machinator.command.LocalMachineCommandExecutor;
import tpiskorski.machinator.core.backup.BackupDefinition;

@Component
public class BackupJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupJob.class);

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public BackupJob(LocalMachineCommandExecutor localMachineCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        BackupDefinition backupDefinition = (BackupDefinition) mergedJobDataMap.get("backupDefinition");
        System.out.println(backupDefinition.getServer().getAddress());
        LOGGER.info("Performing backup for " + backupDefinition.id());
    }
}
