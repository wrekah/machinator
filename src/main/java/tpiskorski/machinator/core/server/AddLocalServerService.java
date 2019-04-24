package tpiskorski.machinator.core.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.ui.core.PlatformThreadAction;
import tpiskorski.machinator.ui.core.PlatformThreadUpdater;

import java.io.IOException;
import java.util.List;

@Service
public class AddLocalServerService {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private CommandFactory commandFactory;
    @Autowired private ServerService serverService;
    @Autowired private VmDetailsService vmDetailsService;
    @Autowired private PlatformThreadUpdater platformThreadUpdater;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public void add(Server server) throws IOException, InterruptedException {
        CommandResult result = ping();

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        result = listAllVms();

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        List<VirtualMachine> vms = simpleVmParser.parse(result);
        vmDetailsService.enrichVms(vms);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));

        platformThreadUpdater.runLater(addServerAndVmsAction(server, vms));
    }

    PlatformThreadAction addServerAndVmsAction(Server server, List<VirtualMachine> vms) {
        return () -> {
            serverService.add(server);
            serverService.upsert(server, vms);
        };
    }

    private CommandResult listAllVms() throws IOException, InterruptedException {
        CommandResult result;
        Command command = commandFactory.make(BaseCommand.LIST_ALL_VMS);
        result = localMachineCommandExecutor.execute(command);
        return result;
    }

    private CommandResult ping() throws IOException, InterruptedException {
        Command command = commandFactory.make(BaseCommand.IS_VBOX_INSTALLED);
        return localMachineCommandExecutor.execute(command);
    }
}
