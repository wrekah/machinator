package tpiskorski.machinator.lifecycle.state.serialize.model;

import tpiskorski.machinator.model.backup.BackupDefinition;

import java.io.Serializable;

public class SerializableBackupDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private SerializableServer serializableServer;
    private SerializableVirtualMachine serializableVirtualMachine;

    private int startAtDayOfTheMonth;
    private int repeatInDays;
    private int hour;
    private int fileLimit;

    public SerializableBackupDefinition(BackupDefinition backupDefinition) {
        this.serializableServer = new SerializableServer(backupDefinition.getServer());
        this.serializableVirtualMachine = new SerializableVirtualMachine(backupDefinition.getVm());
        this.startAtDayOfTheMonth = backupDefinition.getStartAtDayOfTheMonth();
        this.repeatInDays = backupDefinition.getRepeatInDays();
        this.hour = backupDefinition.getHour();
        this.fileLimit = backupDefinition.getFileLimit();
    }

    public BackupDefinition toBackup() {
        BackupDefinition backupDefinition = new BackupDefinition(
            serializableServer.toServer(), serializableVirtualMachine.toVirtualMachine()
        );

        backupDefinition.setStartAtDayOfTheMonth(startAtDayOfTheMonth);
        backupDefinition.setRepeatInDays(repeatInDays);
        backupDefinition.setHour(hour);
        backupDefinition.setFileLimit(fileLimit);

        return backupDefinition;
    }
}
