package tpiskorski.machinator.quartz.backup;

import tpiskorski.machinator.command.CommandResult;

class ExportVmResultInterpreter {
    boolean isSuccess(CommandResult result) {
        return result.getStd().startsWith("Successfully exported");
    }
}
