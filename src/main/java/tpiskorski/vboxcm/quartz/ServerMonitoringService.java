package tpiskorski.vboxcm.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.command.*;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerType;
import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class ServerMonitoringService {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private VmDetailsService vmDetailsService;
    @Autowired private CommandFactory commandFactory;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public List<VirtualMachine> monitor(Server server) throws IOException, InterruptedException {
        if (server.getServerType() == ServerType.LOCAL) {
            return monitorLocalMachine(server);
        } else {
            throw new UnsupportedOperationException("TODO: implement remote");
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
}
