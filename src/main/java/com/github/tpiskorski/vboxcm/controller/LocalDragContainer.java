package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.VirtualMachine;
import org.springframework.stereotype.Repository;

@Repository
public class LocalDragContainer {

    private VirtualMachine virtualMachine;

    public void putVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }
}
