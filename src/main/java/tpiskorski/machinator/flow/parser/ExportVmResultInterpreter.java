package tpiskorski.machinator.flow.parser;

import tpiskorski.machinator.flow.command.CommandResult;

public class ExportVmResultInterpreter {
    public boolean isSuccess(CommandResult result) {
        return result.getStd().startsWith("Successfully exported");
    }
}
