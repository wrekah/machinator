package tpiskorski.machinator.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public VmDeleteJob(LocalMachineCommandExecutor localMachineCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        Command command = commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName());

        try {
            CommandResult result = localMachineCommandExecutor.execute(command);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmDeleteJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
