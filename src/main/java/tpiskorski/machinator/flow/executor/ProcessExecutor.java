package tpiskorski.machinator.flow.executor;

import tpiskorski.machinator.flow.command.Command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor {

    private static final int TIMEOUT = 5;

    public Process execute(Command command, File directory) throws InterruptedException, IOException {
        ProcessBuilder builder = processBuilder(command, directory);

        Process process = builder.start();
        process.waitFor(TIMEOUT, TimeUnit.SECONDS);

        return process;
    }

    private ProcessBuilder processBuilder(Command command, File directory) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(directory);
        builder.command(command.getParts());
        return builder;
    }
}