package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.flow.quartz.service.CleanupService;
import tpiskorski.machinator.flow.quartz.service.StartVmService;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;
import tpiskorski.machinator.model.watchdog.Watchdog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WatchdogJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;
    private final ConfigService configService;

    private PollExecutor pollExecutor = new PollExecutor();
    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private ScpClient scpClient = new ScpClient();

    @Autowired private StartVmService startVmService;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;

    @Autowired
    public WatchdogJob(CommandExecutor commandExecutor, CommandFactory commandFactory, ConfigService configService) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
        this.configService = configService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        Watchdog watchdog = (Watchdog) mergedJobDataMap.get("watchdog");
        LOGGER.info("Watchdog started for {}", watchdog.id());

        VirtualMachine vm = watchdog.getVirtualMachine();

        vm.lock();
        try {
            startVmService.start(vm);
            boolean isRunning = checkIfRunning(vm);
            if (isRunning) {
                LOGGER.info("Watchdog successfully restarted vm");
                return;
            }

            LOGGER.warn("Watchdog was not able to restart vm.");

            Server watchdogServer = watchdog.getWatchdogServer();
            if (watchdogServer == null) {
                LOGGER.warn("No backup server is defined");
                throw new JobExecutionException("No backup server is defined");
            }

            Server originalServer = watchdog.getVirtualMachine().getServer();
            File backupLocation = new File(configService.getConfig().getBackupLocation() + "/" + originalServer.getAddress() + "/" + watchdog.getVirtualMachine().getVmName());
            backupLocation.mkdirs();

            long count = Files.list(backupLocation.toPath()).count();
            if (count == 0) {
                LOGGER.error("No backups found for given vm");
                throw new JobExecutionException("No backups found for given vm");
            }

            String backupFilePath = findLatestBackup(watchdog);

            if (watchdogServer.getServerType() == ServerType.LOCAL) {
                importVm(watchdogServer, backupFilePath);

                startVmService.start(vm);

                ExecutionContext deleteVm = ExecutionContext.builder()
                    .executeOn(originalServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
                    .build();

                CommandResult result = commandExecutor.execute(deleteVm);
                if (!progressCommandsInterpreter.isSuccess(result)) {
                    vm.setState(vmInfoService.state(vm));
                    throw new JobExecutionException(result.getError());
                }
            } else {
                RemoteContext remoteContext = RemoteContext.of(watchdogServer);

                scpClient.copyLocalToRemote(remoteContext, backupLocation.toString(), "~", backupFilePath + ".ova");
                importVm(watchdogServer, backupFilePath);

                startVmService.start(vm);
                cleanupService.cleanup(watchdogServer, "~/" + backupFilePath + ".ova");

                ExecutionContext deleteVm = ExecutionContext.builder()
                    .executeOn(originalServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
                    .build();

                CommandResult result = commandExecutor.execute(deleteVm);
                if (!progressCommandsInterpreter.isSuccess(result)) {
                    vm.setState(vmInfoService.state(vm));
                    throw new JobExecutionException(result.getError());
                }
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

    private void importVm(Server destination, String tempFileName) throws IOException, InterruptedException {
        ExecutionContext importVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.IMPORT_VM, tempFileName))
            .executeOn(destination)
            .build();

        CommandResult execute = commandExecutor.execute(importVm);
    }

    private boolean checkIfRunning(VirtualMachine vm) throws JobExecutionException {
        pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.RUNNING);
        vm.setState(VirtualMachineState.RUNNING);
        return true;
    }
}
