package tpiskorski.machinator.flow.parser;

import tpiskorski.machinator.flow.command.CommandResult;

public class VmResetResultInterpreter {
    public boolean isSuccess(CommandResult result) {
        return !result.getError().endsWith("is not currently running");
    }
}
