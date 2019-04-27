package tpiskorski.machinator.flow.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.Command;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.command.CommandResultFactory;

import java.io.File;
import java.io.IOException;

@Component
public class LocalExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExecutor.class);

    private ProcessExecutor processExecutor = new ProcessExecutor();
    private CommandResultFactory commandResultFactory = new CommandResultFactory();

    public CommandResult execute(ExecutionContext executionContext) throws IOException, InterruptedException {
        Command command = executionContext.getCommand();
        File workingDirectory = executionContext.getWorkingDirectory();

        LOGGER.debug("Executing command {}", command);
        Process process = processExecutor.executeIn(command, workingDirectory);
        CommandResult result = commandResultFactory.from(process);
        LOGGER.debug("Execution successful");

        return result;
    }
}
