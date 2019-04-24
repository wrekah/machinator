package tpiskorski.machinator.quartz.backup;

import tpiskorski.machinator.command.CommandResult;

public class ExportVmResultInterpreter {
    public boolean isSuccess(CommandResult result) {
        return result.getStd().startsWith("Successfully exported");
    }
}
