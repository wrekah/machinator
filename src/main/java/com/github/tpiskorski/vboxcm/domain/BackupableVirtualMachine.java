package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class BackupableVirtualMachine {

    private ObjectProperty<VirtualMachine> virtualMachine = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> firstBackupDay = new SimpleObjectProperty<>();
    private IntegerProperty frequency = new SimpleIntegerProperty();
    private ObjectProperty<LocalTime> backupTime = new SimpleObjectProperty<>();
    private IntegerProperty fileLimit = new SimpleIntegerProperty();

    public VirtualMachine getVirtualMachine() {
        return virtualMachine.get();
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine.set(virtualMachine);
    }

    public ObjectProperty<VirtualMachine> virtualMachineProperty() {
        return virtualMachine;
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
