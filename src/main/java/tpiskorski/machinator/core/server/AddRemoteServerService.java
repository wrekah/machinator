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
public class AddRemoteServerService {

    @Autowired private CommandFactory commandFactory;
    @Autowired private RemoteCommandExecutor remoteCommandExecutor;
    @Autowired private ServerService serverService;
    @Autowired private RemoteVmDetailsService remoteVmDetailsService;

    @Autowired private PlatformThreadUpdater platformThreadUpdater;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public void add(Server server) throws IOException, InterruptedException {
        RemoteContext remoteContext = RemoteContext.of(server);
        Command command = commandFactory.make(BaseCommand.IS_VBOX_INSTALLED);
        CommandResult result = remoteCommandExecutor.execute(command, remoteContext);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        command = commandFactory.make(BaseCommand.LIST_ALL_VMS);
        result = remoteCommandExecutor.execute(command, remoteContext);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        List<VirtualMachine> vms = simpleVmParser.parse(result);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        remoteVmDetailsService.enrichVms(vms);

        platformThreadUpdater.runLater(addServerAndVmsAction(server, vms));
    }

    PlatformThreadAction addServerAndVmsAction(Server server, List<VirtualMachine> vms) {
        return () -> {
            serverService.add(server);
            serverService.upsert(server, vms);
        };
    }
}
