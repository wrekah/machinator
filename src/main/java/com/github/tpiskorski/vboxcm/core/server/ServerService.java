package com.github.tpiskorski.vboxcm.core.server;

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final VirtualMachineService virtualMachineService;

    @Autowired public ServerService(ServerRepository serverRepository, VirtualMachineService virtualMachineService) {
        this.serverRepository = serverRepository;
        this.virtualMachineService = virtualMachineService;
    }

    public void remove(Server serverToRemove) {
        serverRepository.remove(serverToRemove);
        virtualMachineService.removeByServer(serverToRemove);
    }

    public ObservableList<Server> getServers() {
        return serverRepository.getServersList();
    }

    public void add(Server server) {
        serverRepository.add(server);
    }
}
