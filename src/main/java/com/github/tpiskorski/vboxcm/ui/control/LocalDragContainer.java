package com.github.tpiskorski.vboxcm.ui.control;

import com.github.tpiskorski.vboxcm.vm.VirtualMachine;
import org.springframework.stereotype.Repository;

@Repository
public class LocalDragContainer {

    private VirtualMachine virtualMachine;

    void putVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }
}
