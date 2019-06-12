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
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.parser.ExportVmResultInterpreter;
import tpiskorski.machinator.model.server.Server;

@Service
public class ExportVmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportVmService.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();

    @Autowired
    public ExportVmService(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void exportVm(Server server, String exportPath, String vmName) {
        LOGGER.info("Starting vm exporting of {} on {} to {}", vmName, server, exportPath);

        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, exportPath, vmName))
            .build();

        CommandResult result = commandExecutor.execute(exportVm);

        if (!exportVmResultInterpreter.isSuccess(result)) {
            LOGGER.error("Exporting vm {} on server {} to file {} failed", vmName, server, exportPath);
            throw new ExecutionException(result.getError());
        }
        LOGGER.info("Finished vm exporting");
    }
}
