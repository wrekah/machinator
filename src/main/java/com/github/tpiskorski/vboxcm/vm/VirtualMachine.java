package com.github.tpiskorski.vboxcm.vm;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class VirtualMachine {

    private StringProperty vmName = new SimpleStringProperty();
    private StringProperty state = new SimpleStringProperty();
    private StringProperty server = new SimpleStringProperty();
    private IntegerProperty cpuCores = new SimpleIntegerProperty();
    private IntegerProperty ramMemory = new SimpleIntegerProperty();


    public VirtualMachine(){}

    public VirtualMachine(String server, String vmName){
        this.server.set(server);
        this.vmName.set(vmName);
    }

    static Callback<VirtualMachine, Observable[]> extractor() {
        return (VirtualMachine vm) -> new Observable[]{vm.vmNameProperty(), vm.stateProperty(), vm.serverProperty(), vm.cpuCoresProperty(), vm.ramMemoryProperty()};
    }

    public String getVmName() {
        return vmName.get();
    }

    public void setVmName(String vmName) {
        this.vmName.set(vmName);
    }

    public StringProperty vmNameProperty() {
        return vmName;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public StringProperty stateProperty() {
        return state;
    }

    public String getServer() {
        return server.get();
    }

    public void setServer(String server) {
        this.server.set(server);
    }

    public StringProperty serverProperty() {
        return server;
    }

    public int getCpuCores() {
        return cpuCores.get();
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores.set(cpuCores);
    }

    public IntegerProperty cpuCoresProperty() {
        return cpuCores;
    }

    public int getRamMemory() {
        return ramMemory.get();
    }

    public void setRamMemory(int ramMemory) {
        this.ramMemory.set(ramMemory);
    }

    public IntegerProperty ramMemoryProperty() {
        return ramMemory;
    }

    @Override public int hashCode() {
        return Objects.hash(server, vmName);
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof VirtualMachine)) {
            return false;
        }
        VirtualMachine that = (VirtualMachine) obj;

        return Objects.equals(this.getServer(), that.getServer())
            && Objects.equals(this.getVmName(), that.getVmName());
    }

    @Override
    public String toString() {
        return "VirtualMachine{" +
            "vmName=" + vmName +
            ", state=" + state +
            ", server=" + server +
            ", cpuCores=" + cpuCores +
            ", ramMemory=" + ramMemory +
            '}';
    }
}
