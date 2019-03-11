package com.github.tpiskorski.vboxcm.core.vm;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import java.util.Objects;

public class VirtualMachine {

    private StringProperty vmName = new SimpleStringProperty();
    private ObjectProperty<VirtualMachineState> state = new SimpleObjectProperty<>();
    private StringProperty server = new SimpleStringProperty();
    private IntegerProperty cpuCores = new SimpleIntegerProperty();
    private IntegerProperty ramMemory = new SimpleIntegerProperty();

    public VirtualMachine() {
    }

    public VirtualMachine(String server, String vmName) {
        this.server.set(server);
        this.vmName.set(vmName);
        setState(VirtualMachineState.UNREACHABLE);
    }

    static Callback<VirtualMachine, Observable[]> extractor() {
        return (VirtualMachine vm) -> new Observable[]{vm.vmNameProperty(), vm.serverProperty(), vm.cpuCoresProperty(), vm.ramMemoryProperty(), vm.stateProperty()};
    }

    public VirtualMachineState getState() {
        return state.get();
    }

    public void setState(VirtualMachineState state) {
        this.state.set(state);
    }

    public ObjectProperty<VirtualMachineState> stateProperty() {
        return state;
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
        return Objects.hash(getServer(), getVmName());
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
