package tpiskorski.machinator.core.vm;

public enum VirtualMachineState {
    ON,
    OFF,
    UNREACHABLE;

    public static VirtualMachineState parse(String vmState) {
        if (vmState.equals("\"poweroff\"")) {
            return OFF;
        } else {
            return UNREACHABLE;
        }
    }
}
