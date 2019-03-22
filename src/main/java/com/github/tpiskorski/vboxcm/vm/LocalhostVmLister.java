package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalhostVmLister {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalhostVmLister.class);

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    List<VirtualMachine> list() throws InterruptedException, IOException {
        LOGGER.info("Checking localhost vms...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(System.getProperty("user.home")));
        builder.command("sh", "-c", "VBoxManage list vms");

        Process process = builder.start();
        process.waitFor(5, TimeUnit.SECONDS);
        LOGGER.info("Localhost connection successful");
        CommandResult from = CommandResult.from(process);

        return simpleVmParser.parse(from);
    }
}

