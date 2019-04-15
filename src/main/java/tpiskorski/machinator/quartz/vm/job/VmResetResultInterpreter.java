package tpiskorski.machinator.quartz.vm.job;

import tpiskorski.machinator.command.CommandResult;

public class VmResetResultInterpreter {
    public boolean isSuccess(CommandResult result) {
        return !result.getError().endsWith("is not currently running");
    }
}
