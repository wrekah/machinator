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
import tpiskorski.machinator.flow.command.Command;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.flow.parser.ShowVmInfoParser;
import tpiskorski.machinator.flow.parser.ShowVmInfoUpdate;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;

@Component
public class VmPowerOffJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmPowerOffJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private PollExecutor pollExecutor = new PollExecutor();

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

        vm.lock();
        try {
            CommandResult result = commandExecutor.execute(powerOff);

            if (!progressCommandsInterpreter.isSuccess(result)) {
                throw new JobExecutionException(result.getError());
            }

            pollExecutor.pollExecute(() -> {
                ShowVmInfoUpdate update = showVmInfoParser.parse(commandExecutor.execute(infoVm));
                return update.getState() == VirtualMachineState.POWEROFF;
            });

            vm.setState(VirtualMachineState.POWEROFF);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmPowerOffJob job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }
}
