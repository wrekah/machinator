package tpiskorski.machinator.quartz.vm.job;

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
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;

@Component
public class VmTurnOnJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOnJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public VmTurnOnJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        vm.lock();

        //todo error handling
        ExecutionContext startVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
            .build();

        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .build();

        try {
            CommandResult result;

            result = commandExecutor.execute(startVm);
            result = commandExecutor.execute(infoVm);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);
            vm.setState(update.getState());

            vm.unlock();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmTurnOnJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
