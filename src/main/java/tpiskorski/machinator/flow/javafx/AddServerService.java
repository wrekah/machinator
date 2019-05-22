package tpiskorski.machinator.flow.javafx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.quartz.service.VmLister;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.ui.core.PlatformThreadAction;
import tpiskorski.machinator.ui.core.PlatformThreadUpdater;

import java.util.List;

@Service
public class AddServerService {

    @Autowired private CommandFactory commandFactory;
    @Autowired private CommandExecutor commandExecutor;

    @Autowired private ServerService serverService;

    @Autowired private PlatformThreadUpdater platformThreadUpdater;
    @Autowired private VmLister vmLister;

    public void add(Server server) {
        ExecutionContext isVboxInstalled = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.IS_VBOX_INSTALLED))
            .build();

        CommandResult result = commandExecutor.execute(isVboxInstalled);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        List<VirtualMachine> vms = vmLister.list(server);
        platformThreadUpdater.runLater(addServerAndVmsAction(server, vms));
    }

    PlatformThreadAction addServerAndVmsAction(Server server, List<VirtualMachine> vms) {
        return () -> {
            serverService.add(server);
            serverService.refresh(server, vms);
        };
    }
}
