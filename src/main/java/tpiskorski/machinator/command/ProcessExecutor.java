package tpiskorski.machinator.command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor {

    private static final int TIMEOUT = 5;
    private static final String WORK_DIR = "user.home";

    public Process execute(Command command) throws InterruptedException, IOException {
        ProcessBuilder builder = processBuilder(command, new File(System.getProperty(WORK_DIR)));

        Process process = builder.start();
        process.waitFor(TIMEOUT, TimeUnit.SECONDS);

        return process;
    }

    public Process executeIn(Command command, String directoryPath) throws InterruptedException, IOException {
        ProcessBuilder builder = processBuilder(command, new File(directoryPath));

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
