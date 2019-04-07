package tpiskorski.machinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;

@Service
public class LocalVmStarter {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private CommandFactory commandFactory;

    public void startVm(VirtualMachine vm) throws IOException, InterruptedException {
        Command command = commandFactory.make(BaseCommand.START_VM, vm.getVmName());
        CommandResult commandResult = localMachineCommandExecutor.execute(command);
    }
}
