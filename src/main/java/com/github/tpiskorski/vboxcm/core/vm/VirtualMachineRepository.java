package com.github.tpiskorski.vboxcm.core.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualMachineRepository {

    private ObservableList<VirtualMachine> vmObservableList = FXCollections.observableArrayList(VirtualMachine.extractor());

    void add(VirtualMachine vm) {
        vmObservableList.add(vm);
    }

    ObservableList<VirtualMachine> getVms() {
        return vmObservableList;
    }

    void remove(VirtualMachine vm) {
        vmObservableList.remove(vm);
    }

    ObservableList<VirtualMachine> getVms(Server server) {
        return vmObservableList.filtered(
            virtualMachine -> virtualMachine.getServer().equals(server)
        );
    }

    void removeByServer(Server serverToRemove) {
        vmObservableList.removeIf(virtualMachine -> virtualMachine.getServer().equals(serverToRemove));
    }

    public void upsert(VirtualMachine vm) {
        if (!vmObservableList.contains(vm)) {
            add(vm);
        } else {
            VirtualMachine virtualMachine1 = vmObservableList.stream()
                .filter(virtualMachine -> virtualMachine.getId().equals(vm.getId()))
                .findFirst().get();

            virtualMachine1.setState(vm.getState());
            virtualMachine1.setCpuCores(vm.getCpuCores());
            virtualMachine1.setRamMemory(vm.getRamMemory());
            virtualMachine1.setVmName(vm.getVmName());
        }
    }
}
