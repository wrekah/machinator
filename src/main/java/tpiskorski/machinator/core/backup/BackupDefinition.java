package tpiskorski.machinator.core.backup;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.util.Objects;

public class BackupDefinition {

    private Server server;
    private VirtualMachine vm;

    private int startAtDayOfTheMonth;
    private int repeatInDays;
    private int hour;
    private int fileLimit;

    private BooleanProperty active = new SimpleBooleanProperty();

    public BackupDefinition(Server server, VirtualMachine vm) {
        this.server = server;
        this.vm = vm;
        setActive(false);
    }

    static Callback<BackupDefinition, Observable[]> extractor() {
        return (BackupDefinition backupDefinition) -> new Observable[]{backupDefinition.activeProperty()};
    }

    public int getStartAtDayOfTheMonth() {
        return startAtDayOfTheMonth;
    }

    public void setStartAtDayOfTheMonth(int startAtDayOfTheMonth) {
        this.startAtDayOfTheMonth = startAtDayOfTheMonth;
    }

    public int getRepeatInDays() {
        return repeatInDays;
    }

    public void setRepeatInDays(int repeatInDays) {
        this.repeatInDays = repeatInDays;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getFileLimit() {
        return fileLimit;
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit = fileLimit;
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

    public String id() {
        return server.getAddress() + "-" + vm.getVmName();
    }
}
