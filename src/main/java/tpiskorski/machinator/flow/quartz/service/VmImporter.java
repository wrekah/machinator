package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.model.server.Server;

@Service
public class VmImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmImporter.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public VmImporter(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void importVm(Server server, String backupPath) {
        LOGGER.info("Starting vm import of {} with {}", server, backupPath);
        ExecutionContext importVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.IMPORT_VM, backupPath))
            .executeOn(server)
            .build();

        CommandResult result = commandExecutor.execute(importVm);
        if (result.getError().contains("VBOX_E_IPRT_ERROR")) {
            throw new RuntimeException(result.getError());
        }

        LOGGER.info("Finished vm import");
    }
}
