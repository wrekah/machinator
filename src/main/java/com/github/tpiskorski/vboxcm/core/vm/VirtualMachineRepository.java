package com.github.tpiskorski.vboxcm.core.vm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
class VirtualMachineRepository {

    private ObservableList<VirtualMachine> vmObservableList = FXCollections.observableArrayList(VirtualMachine.extractor());

    void add(VirtualMachine vm) {
        vmObservableList.add(vm);
    }

    ObservableList<VirtualMachine> getServersList() {
        return vmObservableList;
    }

    void remove(VirtualMachine vm) {
        vmObservableList.remove(vm);
    }
}
