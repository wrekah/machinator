package tpiskorski.vboxcm.shutdown.state.persist;

import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.io.Serializable;

public class SerializableVirtualMachine implements Serializable {

    private static final long serialVersionUID = 1L;

    private SerializableServer serializableServer;
    private String id;

    public SerializableVirtualMachine(VirtualMachine virtualMachine) {
        this.serializableServer = new SerializableServer(virtualMachine.getServer());
        this.id = virtualMachine.getId();
    }

    public VirtualMachine toVirtualMachine() {
        return new VirtualMachine(serializableServer.toServer(), id);
    }
}
