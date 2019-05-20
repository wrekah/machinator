package tpiskorski.machinator.flow.executor;

import tpiskorski.machinator.flow.command.Command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor {

    private static final int TIMEOUT = 5;

    public Process execute(Command command, File directory) throws ExecutionException {
        ProcessBuilder builder = processBuilder(command, directory);
        return startProcess(builder);
    }

    private ProcessBuilder processBuilder(Command command, File directory) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(directory);
        builder.command(command.getParts());
        return builder;
    }

    private Process startProcess(ProcessBuilder builder) throws ExecutionException {
        Process process;
        try {
            process = builder.start();
            process.waitFor(TIMEOUT, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        }
        return process;
    }
}
