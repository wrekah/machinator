package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ShowVmInfoParser;
import tpiskorski.machinator.flow.parser.ShowVmStateParser;
import tpiskorski.machinator.flow.parser.VmInfo;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

@Service
public class VmInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmInfoService.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;
    private final ShowVmInfoParser showVmInfoParser;
    private final ShowVmStateParser showVmStateParser;

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    public VmInfoService(CommandExecutor commandExecutor, CommandFactory commandFactory, ShowVmInfoParser showVmInfoParser, ShowVmStateParser showVmStateParser) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
        this.showVmInfoParser = showVmInfoParser;
        this.showVmStateParser = showVmStateParser;
    }

    public VmInfo info(VirtualMachine vm) {
        LOGGER.debug("Getting info on vm {}", vm);

        ExecutionContext showVmInfo = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .executeOn(vm.getServer())
            .build();

        CommandResult result = commandExecutor.execute(showVmInfo);

        LOGGER.debug("Got info on vm {}", vm);

        return showVmInfoParser.parse(result);
    }

    public VirtualMachineState state(VirtualMachine vm) {
        LOGGER.debug("Getting state info on vm {}", vm);

        ExecutionContext showVmInfo = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .executeOn(vm.getServer())
            .build();

        CommandResult result = commandExecutor.execute(showVmInfo);

        LOGGER.debug("Got state info on vm {}", vm);
        return showVmStateParser.parse(result);
    }

    public void pollState(VirtualMachine vm, VirtualMachineState state) {
        pollExecutor.pollExecute(() -> state(vm) == state);
    }
}
