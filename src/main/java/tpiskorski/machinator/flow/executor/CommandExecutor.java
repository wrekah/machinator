package tpiskorski.machinator.flow.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tpiskorski.machinator.flow.command.CommandResult;

import java.io.IOException;

public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private final LocalExecutor localExecutor;
    private final RemoteExecutor remoteExecutor;

    public CommandExecutor(LocalExecutor localExecutor, RemoteExecutor remoteExecutor) {
        this.localExecutor = localExecutor;
        this.remoteExecutor = remoteExecutor;
    }

    public CommandResult execute(ExecutionContext executionContext) throws IOException, InterruptedException {
        LOGGER.debug("Executing command {}", executionContext);
        if (executionContext.isLocal()) {
            return localExecutor.execute(executionContext);
        } else {
            return remoteExecutor.execute(executionContext);
        }
    }
}
