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
import tpiskorski.machinator.core.vm.VirtualMachineService;

import java.io.IOException;
import java.util.List;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired private VirtualMachineService virtualMachineService;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    @Autowired
    public VmDeleteJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        //todo error handling
        Command deleteVmCommand = commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName());
        Command listAllVmsCommand = commandFactory.makeWithArgs(BaseCommand.LIST_ALL_VMS, vm.getId());

        ExecutionContext deleteVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(deleteVmCommand)
            .build();

        ExecutionContext listVms = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(listAllVmsCommand)
            .build();

        try {
            CommandResult result = commandExecutor.execute(deleteVm);
            result = commandExecutor.execute(listVms);
            List<VirtualMachine> vms = simpleVmParser.parse(result);
            if (!vms.contains(vm)) {
                virtualMachineService.remove(vm);
            } else {
                throw new JobExecutionException("Could not delete vm");
            }

            ShowVmInfoUpdate update = showVmInfoParser.parse(result);
            vm.setState(update.getState());
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmDeleteJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
