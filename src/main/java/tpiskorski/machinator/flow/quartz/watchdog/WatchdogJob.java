package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
import tpiskorski.machinator.flow.quartz.service.VmImporter;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.flow.quartz.service.VmManipulator;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;
import tpiskorski.machinator.model.watchdog.Watchdog;

import java.io.File;
import java.nio.file.Files;

public class WatchdogJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogJob.class);

    private final ConfigService configService;

    private PollExecutor pollExecutor = new PollExecutor();
    private ScpClient scpClient = new ScpClient();

    @Autowired private VmManipulator vmManipulator;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;
    @Autowired private VmImporter vmImporter;

    @Autowired
    public WatchdogJob(ConfigService configService) {
        this.configService = configService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        Watchdog watchdog = (Watchdog) mergedJobDataMap.get("watchdog");
        LOGGER.info("Watchdog started for {}", watchdog.id());

        VirtualMachine vm = watchdog.getVirtualMachine();

        vm.lock();
        try {
            vmManipulator.start(vm);
            boolean isRunning = checkIfRunning(vm);
            if (isRunning) {
                LOGGER.info("Watchdog successfully restarted vm");
                return;
            }

            LOGGER.warn("Watchdog was not able to restart vm.");

            Server watchdogServer = watchdog.getWatchdogServer();
            if (watchdogServer == null) {
                LOGGER.warn("No backup server is defined");
                throw new ExecutionException("No backup server is defined");
            }

            Server originalServer = watchdog.getVirtualMachine().getServer();
            File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + originalServer.getAddress() + "/" + watchdog.getVirtualMachine().getVmName());
            backupLocation.mkdirs();

            long count = Files.list(backupLocation.toPath()).count();
            if (count == 0) {
                LOGGER.error("No backups found for given vm");
                throw new ExecutionException("No backups found for given vm");
            }

            String backupFilePath = findLatestBackup(watchdog);

            if (watchdogServer.getServerType() == ServerType.LOCAL) {
                vmImporter.importVm(watchdogServer, backupFilePath);
                vmManipulator.start(vm);
                vmManipulator.remove(originalServer, vm.getVmName());
            } else {
                RemoteContext remoteContext = RemoteContext.of(watchdogServer);

                scpClient.copyLocalToRemote(remoteContext, backupLocation.toString(), "~", backupFilePath + ".ova");
                vmImporter.importVm(watchdogServer, backupFilePath);

                vmManipulator.start(vm);
                cleanupService.cleanup(watchdogServer, "~/" + backupFilePath + ".ova");

                vmManipulator.remove(originalServer, vm.getVmName());
            }
        } catch (Exception e) {
            LOGGER.error("Watchdog job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    //todo to implement
    private String findLatestBackup(Watchdog watchdog) {
        return null;
    }

    private boolean checkIfRunning(VirtualMachine vm) throws JobExecutionException {
        pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.RUNNING);
        vm.setState(VirtualMachineState.RUNNING);
        return true;
    }
}
