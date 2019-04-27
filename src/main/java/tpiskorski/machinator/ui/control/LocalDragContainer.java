package tpiskorski.machinator.ui.control;

import tpiskorski.machinator.model.vm.VirtualMachine;
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
