package tpiskorski.machinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.action.CommandExecutor;
import tpiskorski.machinator.action.ExecutionContext;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class VmDetailsService {

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public VmDetailsService(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void enrichVms(List<VirtualMachine> vms) throws IOException, InterruptedException {
        for (VirtualMachine vm : vms) {
            ExecutionContext showVmInfo = ExecutionContext.builder()
                .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
                .executeOn(vm.getServer())
                .build();

            CommandResult result = commandExecutor.execute(showVmInfo);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);

            vm.setCpuCores(update.getCpus());
            vm.setRamMemory(update.getMemory());
            vm.setState(update.getState());
        }
    }
}
