package com.github.tpiskorski.vboxcm.core.server;

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final VirtualMachineService virtualMachineService;

    @Autowired public ServerService(ServerRepository serverRepository, VirtualMachineService virtualMachineService) {
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

    public void updateUnreachable(Server server) {
        server.setServerState(ServerState.NOT_REACHABLE);
        virtualMachineService.updateNotReachableBy(server);
    }

    public void updateReachable(Server server, List<VirtualMachine> vms) {
        server.setServerState(ServerState.REACHABLE);
        virtualMachineService.replace(server, vms);
    }

    public boolean contains(Server server) {
        return serverRepository.contains(server);
    }
}
