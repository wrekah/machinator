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
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.model.vm.VirtualMachine;

@Service
public class PowerOffVmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerOffVmService.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;
    private final ProgressCommandsInterpreter progressCommandsInterpreter;

    @Autowired
    public PowerOffVmService(CommandExecutor commandExecutor, CommandFactory commandFactory, ProgressCommandsInterpreter progressCommandsInterpreter) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
        this.progressCommandsInterpreter = progressCommandsInterpreter;
    }

    public void powerOff(VirtualMachine vm) {
        LOGGER.debug("Powering off vm {}", vm);
        ExecutionContext turnOff = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.TURN_OFF, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(turnOff);

        if (!progressCommandsInterpreter.isSuccess(result)) {
            LOGGER.warn("Powering off vm {} failed", vm);
            throw new ExecutionException(result.getError());
        }
        LOGGER.debug("Powered off vm {}", vm);
    }
}
