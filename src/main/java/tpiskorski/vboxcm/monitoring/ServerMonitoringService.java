package tpiskorski.vboxcm.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.command.Command;
import tpiskorski.vboxcm.command.CommandResult;
import tpiskorski.vboxcm.command.LocalMachineCommandExecutor;
import tpiskorski.vboxcm.command.SimpleVmParser;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerType;
import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class ServerMonitoringService {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public List<VirtualMachine> monitor(Server server) throws IOException, InterruptedException {
        if (server.getServerType() == ServerType.LOCAL) {
            return monitorLocalMachine(server);
        } else {
            throw new UnsupportedOperationException("TODO: implement remote");
        }
    }

    private List<VirtualMachine> monitorLocalMachine(Server server) throws InterruptedException, IOException {
        CommandResult commandResult = localMachineCommandExecutor.execute(Command.of("sh", "-c", "VBoxManage list vms"));
        List<VirtualMachine> vms = simpleVmParser.parse(commandResult);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        return vms;
    }
}
