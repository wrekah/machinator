package tpiskorski.machinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.action.CommandExecutor;
import tpiskorski.machinator.action.ExecutionContext;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class RemoteVmDetailsService {

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public RemoteVmDetailsService(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void enrichVms(List<VirtualMachine> vms) throws IOException, InterruptedException {
        for (VirtualMachine vm : vms) {
            ExecutionContext executionContext = ExecutionContext.builder()
                .executeOn(vm.getServer())
                .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
                .build();

            CommandResult result = commandExecutor.execute(executionContext);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);

            vm.setCpuCores(update.getCpus());
            vm.setRamMemory(update.getMemory());
            vm.setState(update.getState());
        }
    }
}
