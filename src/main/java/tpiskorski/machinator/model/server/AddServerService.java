package tpiskorski.machinator.model.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.command.*;
import tpiskorski.machinator.flow.parser.SimpleVmParser;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.ui.core.PlatformThreadAction;
import tpiskorski.machinator.ui.core.PlatformThreadUpdater;

import java.io.IOException;
import java.util.List;

@Service
public class AddServerService {

    @Autowired private CommandFactory commandFactory;
    @Autowired private CommandExecutor commandExecutor;

    @Autowired private ServerService serverService;
    @Autowired private VmDetailsService vmDetailsService;

    @Autowired private PlatformThreadUpdater platformThreadUpdater;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public void add(Server server) throws IOException, InterruptedException {
        ExecutionContext isVboxInstalled = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.IS_VBOX_INSTALLED))
            .build();

        CommandResult result = commandExecutor.execute(isVboxInstalled);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        ExecutionContext listAllVms
            = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.LIST_ALL_VMS))
            .build();

        result = commandExecutor.execute(listAllVms);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        List<VirtualMachine> vms = simpleVmParser.parse(result);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        vmDetailsService.enrichVms(vms);

        platformThreadUpdater.runLater(addServerAndVmsAction(server, vms));
    }

    PlatformThreadAction addServerAndVmsAction(Server server, List<VirtualMachine> vms) {
        return () -> {
            serverService.add(server);
            serverService.upsert(server, vms);
        };
    }
}
