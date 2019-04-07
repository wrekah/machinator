package tpiskorski.machinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class VmDetailsService {

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();

    @Autowired
    public VmDetailsService(LocalMachineCommandExecutor localMachineCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.commandFactory = commandFactory;
    }

    public void enrichVms(List<VirtualMachine> vms) throws IOException, InterruptedException {
        for (VirtualMachine vm : vms) {
            Command command = commandFactory.make(BaseCommand.SHOW_VM_INFO, vm.getId());
            CommandResult result = localMachineCommandExecutor.execute(command);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);

            vm.setCpuCores(update.getCpus());
            vm.setRamMemory(update.getMemory());
            vm.setState(update.getState());
        }
    }
}
