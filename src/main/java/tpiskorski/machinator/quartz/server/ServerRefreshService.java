package tpiskorski.machinator.quartz.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerType;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class ServerRefreshService {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private RemoteCommandExecutor remoteCommandExecutor;
    @Autowired private VmDetailsService vmDetailsService;
    @Autowired private RemoteVmDetailsService remoteVmDetailsService;
    @Autowired private CommandFactory commandFactory;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public List<VirtualMachine> monitor(Server server) throws IOException, InterruptedException {
        if (server.getServerType() == ServerType.LOCAL) {
            return monitorLocalMachine(server);
        } else {
            return monitorRemoteMachine(server);
        }
    }

    private List<VirtualMachine> monitorLocalMachine(Server server) throws InterruptedException, IOException {
        Command command = commandFactory.make(BaseCommand.LIST_ALL_VMS);
        CommandResult commandResult = localMachineCommandExecutor.execute(command);
        List<VirtualMachine> vms = simpleVmParser.parse(commandResult);
        vmDetailsService.enrichVms(vms);

        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        return vms;
    }

    private List<VirtualMachine> monitorRemoteMachine(Server server) throws IOException, InterruptedException {
        Command command = commandFactory.make(BaseCommand.LIST_ALL_VMS);
        RemoteContext remoteContext = RemoteContext.of(server);

        CommandResult commandResult = remoteCommandExecutor.execute(command, remoteContext);
        List<VirtualMachine> vms = simpleVmParser.parse(commandResult);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        remoteVmDetailsService.enrichVms(vms);

        return vms;
    }
}
