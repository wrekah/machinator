package tpiskorski.vboxcm.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LocalMachineVmLister {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMachineVmLister.class);

    public CommandResult list() throws InterruptedException, IOException {
        LOGGER.info("Checking localhost vms...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(System.getProperty("user.home")));
        builder.command("sh", "-c", "VBoxManage list vms");

        Process process = builder.start();
        process.waitFor(5, TimeUnit.SECONDS);

        LOGGER.info("Localhost connection successful");

        return CommandResult.from(process);
    }
}

