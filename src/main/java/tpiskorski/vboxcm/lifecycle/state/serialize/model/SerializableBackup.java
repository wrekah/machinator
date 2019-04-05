package tpiskorski.vboxcm.lifecycle.state.serialize.model;

import tpiskorski.vboxcm.core.backup.Backup;

import java.io.Serializable;

public class SerializableBackup implements Serializable {

    private static final long serialVersionUID = 1L;

    private SerializableServer serializableServer;
    private SerializableVirtualMachine serializableVirtualMachine;

    public SerializableBackup(Backup backup) {
        this.serializableServer = new SerializableServer(backup.getServer());
        this.serializableVirtualMachine = new SerializableVirtualMachine(backup.getVm());
    }

    public Backup toBackup() {
        return new Backup(serializableServer.toServer(), serializableVirtualMachine.toVirtualMachine());
    }
}
