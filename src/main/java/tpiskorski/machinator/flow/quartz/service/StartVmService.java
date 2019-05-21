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
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.vm.VirtualMachine;

@Service
public class StartVmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartVmService.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public StartVmService(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void start(VirtualMachine vm) {
        LOGGER.debug("Starting vm {}", vm);
        ExecutionContext startVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(startVm);

        if (result.isFailed()) {
            LOGGER.error("Starting vm {} failed {}", vm, result.getError());
            throw new ExecutionException(result.getError());
        }

        LOGGER.debug("Started vm {}", vm);
    }
}
