package tpiskorski.vboxcm.shutdown.state.persist;

import tpiskorski.vboxcm.core.backup.Backup;

public class SerializableBackup {

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
