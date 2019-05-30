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
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.*;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;
import tpiskorski.machinator.model.watchdog.Watchdog;

public class WatchdogJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogJob.class);

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired private VmManipulator vmManipulator;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;
    @Autowired private VmImporter vmImporter;
    @Autowired private CopyService copyService;
    @Autowired private BackupService backupService;

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

            long backupCount = backupService.getBackupCount(vm);
            if (backupCount == 0) {
                LOGGER.error("No backups found for given vm");
                throw new ExecutionException("No backups found for given vm");
            }

            String latestBackupFilePath = backupService.findLatestBackup(vm);

            if (watchdogServer.getServerType() == ServerType.LOCAL) {
                vmImporter.importVm(watchdogServer, latestBackupFilePath);
                vmManipulator.start(vm);
                vmManipulator.remove(originalServer, vm.getVmName());
                vm.setServer(watchdogServer);
            } else {
                String remoteTemporaryFilePath = backupService.getRemoteTemporaryFilePath(vm);

                try {
                    cleanupService.cleanup(watchdogServer, remoteTemporaryFilePath);
                    copyService.copyLocalToRemote(watchdogServer, latestBackupFilePath, remoteTemporaryFilePath);

                    vmImporter.importVm(watchdogServer, remoteTemporaryFilePath);

                    vmManipulator.start(vm);

                    vmManipulator.remove(originalServer, vm.getVmName());
                    vm.setServer(watchdogServer);
                } finally {
                    cleanupService.cleanup(watchdogServer, remoteTemporaryFilePath);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Watchdog job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private boolean checkIfRunning(VirtualMachine vm) throws JobExecutionException {
        pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.RUNNING);
        vm.setState(VirtualMachineState.RUNNING);
        return true;
    }
}
