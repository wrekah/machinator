package tpiskorski.machinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class RemoteVmDetailsService {

    private final RemoteCommandExecutor remoteCommandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public RemoteVmDetailsService(RemoteCommandExecutor remoteCommandExecutor, CommandFactory commandFactory) {
        this.remoteCommandExecutor = remoteCommandExecutor;
        this.commandFactory = commandFactory;
    }

    public void enrichVms(List<VirtualMachine> vms) throws IOException, InterruptedException {
        for (VirtualMachine vm : vms) {
            Command command = commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId());
            RemoteContext remoteContext = RemoteContext.of(vm.getServer());
            CommandResult result = remoteCommandExecutor.execute(command,remoteContext);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);

            vm.setCpuCores(update.getCpus());
            vm.setRamMemory(update.getMemory());
            vm.setState(update.getState());
        }
    }
}
