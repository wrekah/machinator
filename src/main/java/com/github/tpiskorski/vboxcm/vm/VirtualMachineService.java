package com.github.tpiskorski.vboxcm.vm;

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
        return virtualMachineRepository.getServersList();
    }

    public void remove(VirtualMachine vmToRemove) {
        virtualMachineRepository.remove(vmToRemove);
    }
}
