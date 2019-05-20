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
import tpiskorski.machinator.flow.parser.ShowVmInfoParser;
import tpiskorski.machinator.flow.parser.ShowVmInfoUpdate;
import tpiskorski.machinator.flow.parser.ShowVmStateParser;
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
    private ShowVmStateParser showVmStateParser = new ShowVmStateParser();
    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private ScpClient scpClient = new ScpClient();

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
            startVm(vm);
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

                ExecutionContext startVm = ExecutionContext.builder()
                    .executeOn(watchdogServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
                    .build();

                CommandResult result = commandExecutor.execute(startVm);

                if (result.isFailed()) {
                    throw new JobExecutionException(result.getError());
                }

                ExecutionContext deleteVm = ExecutionContext.builder()
                    .executeOn(originalServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
                    .build();


                ExecutionContext infoVm = ExecutionContext.builder()
                    .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
                    .executeOn(originalServer)
                    .build();

                result = commandExecutor.execute(deleteVm);
                if (!progressCommandsInterpreter.isSuccess(result)) {
                    ShowVmInfoUpdate update = showVmInfoParser.parse(commandExecutor.execute(infoVm));
                    vm.setState(update.getState());
                    throw new JobExecutionException(result.getError());
                }
            } else {
                RemoteContext remoteContext = RemoteContext.of(watchdogServer);

                scpClient.copyLocalToRemote(remoteContext, backupLocation.toString(), "~", backupFilePath + ".ova");
                importVm(watchdogServer, backupFilePath);

                ExecutionContext startVm = ExecutionContext.builder()
                    .executeOn(watchdogServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
                    .build();

                CommandResult result = commandExecutor.execute(startVm);


                ExecutionContext cleanup = ExecutionContext.builder()
                    .executeOn(watchdogServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.RM_FILES, "~/" + backupFilePath + ".ova"))
                    .build();
                CommandResult execute2 = commandExecutor.execute(cleanup);


                ExecutionContext deleteVm = ExecutionContext.builder()
                    .executeOn(originalServer)
                    .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
                    .build();


                ExecutionContext infoVm = ExecutionContext.builder()
                    .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
                    .executeOn(originalServer)
                    .build();

                result = commandExecutor.execute(deleteVm);
                if (!progressCommandsInterpreter.isSuccess(result)) {
                    ShowVmInfoUpdate update = showVmInfoParser.parse(commandExecutor.execute(infoVm));
                    vm.setState(update.getState());
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

    private void startVm(VirtualMachine vm) throws JobExecutionException, IOException, InterruptedException {
        ExecutionContext startVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(startVm);

        if (result.isFailed()) {
            throw new JobExecutionException(result.getError());
        }
    }

    private boolean checkIfRunning(VirtualMachine vm) throws JobExecutionException {
        ExecutionContext infoVm = ExecutionContext.builder()
            .executeOn(vm.getServer())
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .build();

        pollExecutor.pollExecute(() -> showVmStateParser.parse(commandExecutor.execute(infoVm)) == VirtualMachineState.RUNNING);
        vm.setState(VirtualMachineState.RUNNING);
        return true;
    }
}
