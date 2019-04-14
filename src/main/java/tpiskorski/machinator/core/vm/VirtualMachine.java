package tpiskorski.machinator.core.vm;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import tpiskorski.machinator.core.server.Server;

import java.util.Objects;

public class VirtualMachine {

    private String id;
    private Server server;

    private StringProperty vmName = new SimpleStringProperty();
    private IntegerProperty cpuCores = new SimpleIntegerProperty();
    private IntegerProperty ramMemory = new SimpleIntegerProperty();
    private ObjectProperty<VirtualMachineState> state = new SimpleObjectProperty<>();

    public VirtualMachine() {
    }

    public VirtualMachine(Server server, String id) {
        setServer(server);
        setId(id);
        setState(VirtualMachineState.UNREACHABLE);
    }

    static Callback<VirtualMachine, Observable[]> extractor() {
        return (VirtualMachine vm) -> new Observable[]{vm.vmNameProperty(), vm.cpuCoresProperty(), vm.ramMemoryProperty(), vm.stateProperty()};
    }

    public String getServerAddress() {
        return server.getSimpleAddress();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
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
        return Objects.hash(getServer(), getId());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof VirtualMachine)) {
            return false;
        }
        VirtualMachine that = (VirtualMachine) obj;

        return Objects.equals(this.getServer(), that.getServer())
            && Objects.equals(this.getId(), that.getId())
            && Objects.equals(this.getState(), that.getState());
    }

    @Override
    public String toString() {
        return getVmName();
    }
}
