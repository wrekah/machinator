package tpiskorski.machinator.core.backup;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class BackupDefinition {

    private Server server;
    private VirtualMachine vm;

    private ObjectProperty<LocalDate> firstBackupDay = new SimpleObjectProperty<>();
    private IntegerProperty frequency = new SimpleIntegerProperty();
    private ObjectProperty<LocalTime> backupTime = new SimpleObjectProperty<>();
    private IntegerProperty currentFiles = new SimpleIntegerProperty();
    private IntegerProperty fileLimit = new SimpleIntegerProperty();
    private BooleanProperty active = new SimpleBooleanProperty();

    public BackupDefinition(Server server, VirtualMachine vm) {
        this.server = server;
        this.vm = vm;
        setActive(false);
    }

    static Callback<BackupDefinition, Observable[]> extractor() {
        return (BackupDefinition backupDefinition) -> new Observable[]{
            backupDefinition.firstBackupDayProperty(), backupDefinition.frequencyProperty(),
            backupDefinition.backupTimeProperty(), backupDefinition.fileLimitProperty(),
            backupDefinition.activeProperty()
        };
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public Server getServer() {
        return server;
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public int getCurrentFiles() {
        return currentFiles.get();
    }

    public void setCurrentFiles(int currentFiles) {
        this.currentFiles.set(currentFiles);
    }

    public IntegerProperty currentFilesProperty() {
        return currentFiles;
    }

    @Override public int hashCode() {
        return Objects.hash(getServer(), getVm());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof BackupDefinition)) {
            return false;
        }
        BackupDefinition that = (BackupDefinition) obj;

        return Objects.equals(this.getServer(), that.getServer()) && Objects.equals(this.getVm(), that.getVm());
    }

    public LocalDate getFirstBackupDay() {
        return firstBackupDay.get();
    }

    public void setFirstBackupDay(LocalDate firstBackupDay) {
        this.firstBackupDay.set(firstBackupDay);
    }

    public ObjectProperty<LocalDate> firstBackupDayProperty() {
        return firstBackupDay;
    }

    public int getFrequency() {
        return frequency.get();
    }

    public void setFrequency(int frequency) {
        this.frequency.set(frequency);
    }

    public IntegerProperty frequencyProperty() {
        return frequency;
    }

    public LocalTime getBackupTime() {
        return backupTime.get();
    }

    public void setBackupTime(LocalTime backupTime) {
        this.backupTime.set(backupTime);
    }

    public ObjectProperty<LocalTime> backupTimeProperty() {
        return backupTime;
    }

    public int getFileLimit() {
        return fileLimit.get();
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit.set(fileLimit);
    }

    public IntegerProperty fileLimitProperty() {
        return fileLimit;
    }
}
