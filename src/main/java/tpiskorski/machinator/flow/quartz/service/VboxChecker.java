package tpiskorski.machinator.flow.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.model.server.Server;

@Service
public class VboxChecker {

    @Autowired private CommandFactory commandFactory;
    @Autowired private CommandExecutor commandExecutor;

    public String getVboxVersion(Server server) {
        ExecutionContext isVboxInstalled = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.IS_VBOX_INSTALLED))
            .build();

        CommandResult result = commandExecutor.execute(isVboxInstalled);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        return result.getStd();
    }
}
