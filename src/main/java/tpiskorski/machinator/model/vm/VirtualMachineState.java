package tpiskorski.machinator.model.vm;

public enum VirtualMachineState {
    COMMAND_IN_PROGRESS,
    REFRESH_IN_PROGRESS,

    ABORTED,
    RUNNING,
    RUNNING_RECENTLY_RESET,
    WAITING_FOR_REFRESH,
    SAVED,
    POWEROFF,
    UNREACHABLE,
    NODE_NOT_REACHABLE,
    COULD_NOT_PARSE;

    public static VirtualMachineState parse(String vmState) {
        if (vmState.equals("\"poweroff\"")) {
            return POWEROFF;
        } else if (vmState.equals("\"running\"")) {
            return RUNNING;
        } else if (vmState.equals("\"aborted\"")) {
            return ABORTED;
        } else if (vmState.equals("\"saved\"")) {
            return SAVED;
        } else {
            return UNREACHABLE;
        }
    }
}
