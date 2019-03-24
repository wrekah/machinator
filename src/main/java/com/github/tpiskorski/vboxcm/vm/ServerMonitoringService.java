package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerType;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ServerMonitoringService {

    private LocalMachineVmLister localMachineVmLister = new LocalMachineVmLister();
    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public List<VirtualMachine> monitor(Server server) throws IOException, InterruptedException {
        if (server.getServerType() == ServerType.LOCAL) {
            return monitorLocalMachine(server);
        } else {
            throw new UnsupportedOperationException("TODO: implement remote");
        }
    }

    private List<VirtualMachine> monitorLocalMachine(Server server) throws InterruptedException, IOException {
        CommandResult commandResult = localMachineVmLister.list();
        List<VirtualMachine> vms = simpleVmParser.parse(commandResult);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        return vms;
    }
}
