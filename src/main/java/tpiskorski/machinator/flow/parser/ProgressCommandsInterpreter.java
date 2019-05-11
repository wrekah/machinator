package tpiskorski.machinator.flow.parser;

import tpiskorski.machinator.flow.command.CommandResult;

public class ProgressCommandsInterpreter {

    public boolean isSuccess(CommandResult commandResult) {
        return commandResult.getError().startsWith("0%...10%...20%...30%...40%...50%...60%...70%...80%...90%...100%");
    }
}
