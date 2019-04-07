package tpiskorski.vboxcm.monitoring;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import tpiskorski.vboxcm.command.*;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.core.server.ServerType;
import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Service
public class AddServerService extends Service<Void> {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private CommandFactory commandFactory;
    @Autowired private ServerService serverService;
    @Autowired private VmDetailsService vmDetailsService;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    private Server server;

    public void start(Server server) {
        this.server = server;
        super.start();
    }

    @Override protected Task<Void> createTask() {
        return new Task<>() {
            @Override protected Void call() throws Exception {

                if (server.getServerType() == ServerType.LOCAL) {
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

                    Platform.runLater(() -> {
                        serverService.add(server);
                        serverService.upsert(server, vms);
                    });

                    return null;
                } else {
                    throw new RuntimeException("Remote servers are not implemented yet");
                }
            }
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
