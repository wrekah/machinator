package tpiskorski.vboxcm.command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor {

    private static final int TIMEOUT = 5;
    private static final String WORK_DIR = "user.home";

    public Process execute(Command command) throws InterruptedException, IOException {
        ProcessBuilder builder = processBuilder(command);

        Process process = builder.start();
        process.waitFor(TIMEOUT, TimeUnit.SECONDS);

        return process;
    }

    private ProcessBuilder processBuilder(Command command) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(System.getProperty(WORK_DIR)));
        builder.command(command.getParts());
        return builder;
    }
}
