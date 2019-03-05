package com.github.tpiskorski.vboxcm.core.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
class VirtualMachineRepository {

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
            virtualMachine -> virtualMachine.getServer().equals(server.getAddress())
        );
    }
}
