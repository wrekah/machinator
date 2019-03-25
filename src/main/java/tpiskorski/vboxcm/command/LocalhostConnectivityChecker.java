package tpiskorski.vboxcm.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LocalhostConnectivityChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalhostConnectivityChecker.class);

    public CommandResult check() throws InterruptedException, IOException {
        LOGGER.info("Checking localhost connection...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(System.getProperty("user.home")));
        builder.command("sh", "-c", "VBoxManage --version");

        Process process = builder.start();
        process.waitFor(5, TimeUnit.SECONDS);
        LOGGER.info("Localhost connection successful");
        return CommandResult.from(process);
    }
}
