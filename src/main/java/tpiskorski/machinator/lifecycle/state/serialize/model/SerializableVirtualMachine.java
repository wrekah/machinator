package tpiskorski.machinator.lifecycle.state.serialize.model;

import tpiskorski.machinator.model.vm.VirtualMachine;

import java.io.Serializable;

public class SerializableVirtualMachine implements Serializable {

    private static final long serialVersionUID = 1L;

    private SerializableServer serializableServer;
    private String id;

    private String vmName;
    private int cpuCores;
    private int ramMemory;

    public SerializableVirtualMachine(VirtualMachine virtualMachine) {
        this.serializableServer = new SerializableServer(virtualMachine.getServer());
        this.id = virtualMachine.getId();
        this.vmName = virtualMachine.getVmName();
        this.cpuCores = virtualMachine.getCpuCores();
        this.ramMemory = virtualMachine.getRamMemory();
    }

    public VirtualMachine toVirtualMachine() {
        VirtualMachine virtualMachine = new VirtualMachine(serializableServer.toServer(), id);
        virtualMachine.setVmName(vmName);
        virtualMachine.setCpuCores(cpuCores);
        virtualMachine.setRamMemory(ramMemory);
        return virtualMachine;
    }
}
