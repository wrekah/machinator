package tpiskorski.machinator.flow.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.vm.VirtualMachine;

@Service
public class VmRemover {

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();

    @Autowired
    public VmRemover(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }


    public void remove(Server server, String vmName){
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
}
