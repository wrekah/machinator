package tpiskorski.machinator.core.vm;

public enum VirtualMachineState {
    COMMAND_IN_PROGRESS,

    ABORTED,
    RUNNING,
    RUNNING_RECENTLY_RESET,
    POWEROFF,
    UNREACHABLE;

    public static VirtualMachineState parse(String vmState) {
        if (vmState.equals("\"poweroff\"")) {
            return POWEROFF;
        } else if (vmState.equals("\"running\"")) {
            return RUNNING;
        } else if (vmState.equals("\"aborted\"")) {
            return ABORTED;
        } else {
            return UNREACHABLE;
        }
    }
}
