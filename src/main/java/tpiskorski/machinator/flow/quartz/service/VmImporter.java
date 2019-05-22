package tpiskorski.machinator.flow.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.model.server.Server;

@Service
public class VmImporter {

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public VmImporter(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void importVm(Server server, String backupPath) {
        ExecutionContext importVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.IMPORT_VM, backupPath))
            .executeOn(server)
            .build();

        commandExecutor.execute(importVm);
    }
}
