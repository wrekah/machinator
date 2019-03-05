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

    ObservableList<VirtualMachine> getVmsByServer() {
        return vmObservableList;
    }

    void remove(VirtualMachine vm) {
        vmObservableList.remove(vm);
    }

    ObservableList<VirtualMachine> getVmsByServer(Server server) {
        return vmObservableList.filtered(
            virtualMachine -> virtualMachine.getServer().equals(server.getAddress())
        );
    }

    void removeByServer(Server serverToRemove) {
        vmObservableList.removeIf(virtualMachine -> virtualMachine.getServer().equals(serverToRemove.getAddress()));
    }
}
