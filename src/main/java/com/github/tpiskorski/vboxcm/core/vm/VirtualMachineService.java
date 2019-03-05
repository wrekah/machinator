package com.github.tpiskorski.vboxcm.core.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;

    @Autowired public VirtualMachineService(VirtualMachineRepository virtualMachineRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
    }

    public void add(VirtualMachine vm) {
        virtualMachineRepository.add(vm);
    }

    public ObservableList<VirtualMachine> getVms() {
        return virtualMachineRepository.getVmsByServer();
    }

    public ObservableList<VirtualMachine> getVms(Server server) {
        return virtualMachineRepository.getVmsByServer(server);
    }

    public void remove(VirtualMachine vmToRemove) {
        virtualMachineRepository.remove(vmToRemove);
    }

    public void removeByServer(Server serverToRemove) {
      virtualMachineRepository.removeByServer(serverToRemove);

    }
}
