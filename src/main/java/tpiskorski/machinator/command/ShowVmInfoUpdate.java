package tpiskorski.machinator.command;

import tpiskorski.machinator.core.vm.VirtualMachineState;

public class ShowVmInfoUpdate {

    private int cpus;
    private int memory;
    private VirtualMachineState state;

    public int getCpus() {
        return cpus;
    }

    public void setCpus(int cpus) {
        this.cpus = cpus;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public VirtualMachineState getState() {
        return state;
    }

    public void setState(VirtualMachineState state) {
        this.state = state;
    }
}