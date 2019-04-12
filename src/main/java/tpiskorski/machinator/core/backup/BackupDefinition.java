package tpiskorski.machinator.core.backup;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Callback;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.util.Objects;

public class BackupDefinition {

    private Server server;
    private VirtualMachine vm;

    private IntegerProperty firstDay = new SimpleIntegerProperty();
    private IntegerProperty dayInterval = new SimpleIntegerProperty();
    private IntegerProperty hourTime = new SimpleIntegerProperty();
    private IntegerProperty fileLimit = new SimpleIntegerProperty();
    private BooleanProperty active = new SimpleBooleanProperty();

    public BackupDefinition(Server server, VirtualMachine vm) {
        this.server = server;
        this.vm = vm;
        setActive(false);
    }

    static Callback<BackupDefinition, Observable[]> extractor() {
        return (BackupDefinition backupDefinition) -> new Observable[]{
            backupDefinition.firstDayProperty(), backupDefinition.dayIntervalProperty(),
            backupDefinition.fileLimitProperty(), backupDefinition.activeProperty()
        };
    }

    public int getHourTime() {
        return hourTime.get();
    }

    public void setHourTime(int hourTime) {
        this.hourTime.set(hourTime);
    }

    public IntegerProperty hourTimeProperty() {
        return hourTime;
    }

    public int getFirstDay() {
        return firstDay.get();
    }

    public void setFirstDay(int firstDay) {
        this.firstDay.set(firstDay);
    }

    public IntegerProperty firstDayProperty() {
        return firstDay;
    }

    public int getDayInterval() {
        return dayInterval.get();
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval.set(dayInterval);
    }

    public IntegerProperty dayIntervalProperty() {
        return dayInterval;
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

    public int getFileLimit() {
        return fileLimit.get();
    }

    public void setFileLimit(int fileLimit) {
        this.fileLimit.set(fileLimit);
    }

    public IntegerProperty fileLimitProperty() {
        return fileLimit;
    }

    public String id() {
        return server.getAddress() + "-" + vm.getId();
    }
}
