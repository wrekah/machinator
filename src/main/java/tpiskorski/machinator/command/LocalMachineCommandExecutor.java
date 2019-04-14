package tpiskorski.machinator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocalMachineCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMachineCommandExecutor.class);

    private ProcessExecutor processExecutor = new ProcessExecutor();
    private CommandResultFactory commandResultFactory = new CommandResultFactory();

    public CommandResult execute(Command command) throws IOException, InterruptedException {
        LOGGER.info("Executing command {}", command);
        Process process = processExecutor.execute(command);
        CommandResult result = commandResultFactory.from(process);
        LOGGER.info("Execution successful");
        return result;
    }

    public CommandResult executeIn(Command command, String directoryPath) throws IOException, InterruptedException {
        LOGGER.info("Executing command {}", command);
        Process process = processExecutor.executeIn(command, directoryPath);
        CommandResult result = commandResultFactory.from(process);
        LOGGER.info("Execution successful");
        return result;
    }
}
