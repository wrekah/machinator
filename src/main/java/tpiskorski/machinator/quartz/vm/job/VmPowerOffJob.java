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
public class VmPowerOffJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmPowerOffJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public VmPowerOffJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        Command powerOffVmCommand = commandFactory.makeWithArgs(BaseCommand.POWER_OFF_VM, vm.getVmName());
        Command infoVmCommand = commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId());

        ExecutionContext powerOff = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(powerOffVmCommand)
            .build();

        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(infoVmCommand)
            .build();

        //todo issue power off, do polling with timeout until state is power off - config option
        try {
            CommandResult result = commandExecutor.execute(powerOff);
            result = commandExecutor.execute(infoVm);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);
            vm.setState(update.getState());
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmPowerOffJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
