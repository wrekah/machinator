package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ShowVmStateParser;
import tpiskorski.machinator.flow.quartz.service.StartVmService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

@Component
public class VmTurnOnJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOnJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private final StartVmService startVmService;

    private PollExecutor pollExecutor = new PollExecutor();
    private ShowVmStateParser showVmStateParser = new ShowVmStateParser();

    @Autowired
    public VmTurnOnJob(CommandExecutor commandExecutor, CommandFactory commandFactory, StartVmService startVmService) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
        this.startVmService = startVmService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        vm.lock();
        try {
            startVmService.start(vm);
            checkIfRunning(vm);
        } finally {
            vm.unlock();
        }
    }

    private void checkIfRunning(VirtualMachine vm) throws JobExecutionException {
        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .build();

        pollExecutor.pollExecute(() -> showVmStateParser.parse(commandExecutor.execute(infoVm)) == VirtualMachineState.RUNNING);
        vm.setState(VirtualMachineState.RUNNING);
    }
}
