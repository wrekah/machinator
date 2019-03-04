package com.github.tpiskorski.vboxcm.vm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualMachineRepository {

    private ObservableList<VirtualMachine> vmObservableList = FXCollections.observableArrayList(VirtualMachine.extractor());

    public void add(VirtualMachine vm) {
        vmObservableList.add(vm);
    }

    public ObservableList<VirtualMachine> getServersList() {
        return vmObservableList;
    }

    public void remove(VirtualMachine vm) {
        vmObservableList.remove(vm);
    }
}
