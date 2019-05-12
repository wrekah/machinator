package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ShowVmStateParser;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;
import tpiskorski.machinator.model.watchdog.Watchdog;

import java.io.IOException;

public class WatchdogJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private PollExecutor pollExecutor = new PollExecutor();
    private ShowVmStateParser showVmStateParser = new ShowVmStateParser();

    @Autowired
    public WatchdogJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        Watchdog watchdog = (Watchdog) mergedJobDataMap.get("watchdog");
        LOGGER.info("Watchdog started for {}", watchdog.id());

        VirtualMachine vm = watchdog.getVirtualMachine();

        vm.lock();
        try {
            startVm(vm);
            checkIfRunning(vm);
            //do it few times
            //check if backup server is defined
            //if so check if theres backup
            //if so try setting it up there
        } catch (Exception e) {

        } finally {
            vm.unlock();
        }
    }

    private void startVm(VirtualMachine vm) throws JobExecutionException, IOException, InterruptedException {
        ExecutionContext startVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(startVm);

        if (result.isFailed()) {
            throw new JobExecutionException(result.getError());
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
