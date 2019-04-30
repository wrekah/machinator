package tpiskorski.machinator.model.server;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;

import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final VirtualMachineService virtualMachineService;

    @Autowired
    public ServerService(ServerRepository serverRepository, VirtualMachineService virtualMachineService) {
        this.serverRepository = serverRepository;
        this.virtualMachineService = virtualMachineService;
    }

    public void remove(Server server) {
        serverRepository.remove(server);
        virtualMachineService.removeByServer(server);
    }

    public ObservableList<Server> getServers() {
        return serverRepository.getServersList();
    }

    public void add(Server server) {
        serverRepository.add(server);
    }

    public void refresh(Server server, List<VirtualMachine> vms) {
        server.setServerState(ServerState.REACHABLE);
        virtualMachineService.refresh(server, vms);
    }

    public boolean contains(Server server) {
        return serverRepository.contains(server);
    }

    public void upsert(Server server, List<VirtualMachine> vms) {
        server.setServerState(ServerState.REACHABLE);
        virtualMachineService.upsert(vms);
    }
}
