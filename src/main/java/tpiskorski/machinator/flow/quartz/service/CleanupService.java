package tpiskorski.machinator.flow.quartz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.server.Server;

@Service
public class CleanupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupService.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    @Autowired
    public CleanupService(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public void cleanup(Server server, String filePath) throws ExecutionException {
        LOGGER.debug("Started cleanup of {} on {}", filePath, server);

        ExecutionContext cleanup = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.makeWithArgs(BaseCommand.RM_FILES, filePath))
            .build();

        commandExecutor.execute(cleanup);

        LOGGER.debug("Finished cleanup of {} on {}", filePath, server);
    }
}
