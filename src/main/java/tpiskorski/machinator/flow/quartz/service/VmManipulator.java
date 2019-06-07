package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.flow.parser.VmResetResultInterpreter;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.vm.VirtualMachine;

@Service
public class VmManipulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmManipulator.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private VmResetResultInterpreter vmResetResultInterpreter = new VmResetResultInterpreter();

    public VmManipulator(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void remove(Server server, String vmName) {
        ExecutionContext deleteVm = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vmName))
            .build();

        CommandResult result = commandExecutor.execute(deleteVm);
        if (!progressCommandsInterpreter.isSuccess(result)) {
            throw new ExecutionException(result.getError());
        }
    }

    public void remove(VirtualMachine vm) {
        ExecutionContext deleteVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(deleteVm);
        if (!progressCommandsInterpreter.isSuccess(result)) {
            throw new ExecutionException(result.getError());
        }
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

    public void turnoff(VirtualMachine vm) {
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

    public void powerOff(VirtualMachine vm) {
        ExecutionContext powerOff = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.POWER_OFF_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(powerOff);

        if (!progressCommandsInterpreter.isSuccess(result)) {
            throw new ExecutionException(result.getError());
        }
    }

    //if running
    public void resetVm(VirtualMachine vm) {
        ExecutionContext resetVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.RESET_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(resetVm);

        if (!vmResetResultInterpreter.isSuccess(result)) {
            throw new ExecutionException(result.getError());
        }
    }
}
