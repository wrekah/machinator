package tpiskorski.vboxcm.core.backup;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.vm.VirtualMachine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Backup {

    private Server server;
    private VirtualMachine vm;

    private ObjectProperty<LocalDate> firstBackupDay = new SimpleObjectProperty<>();
    private IntegerProperty frequency = new SimpleIntegerProperty();
    private ObjectProperty<LocalTime> backupTime = new SimpleObjectProperty<>();
    private IntegerProperty currentFiles = new SimpleIntegerProperty();
    private IntegerProperty fileLimit = new SimpleIntegerProperty();
    private BooleanProperty active = new SimpleBooleanProperty();

    public Backup(Server server, VirtualMachine vm) {
        setServer(server);
        setVm(vm);
        setActive(false);
    }

    static Callback<Backup, Observable[]> extractor() {
        return (Backup backup) -> new Observable[]{
            backup.firstBackupDayProperty(), backup.frequencyProperty(),
            backup.backupTimeProperty(), backup.fileLimitProperty(),
            backup.activeProperty()
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

    public void setServer(Server server) {
        this.server = server;
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public void setVm(VirtualMachine vm) {
        this.vm = vm;
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
        if (!(obj instanceof Backup)) {
            return false;
        }
        Backup that = (Backup) obj;

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
