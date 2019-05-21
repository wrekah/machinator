package tpiskorski.machinator.flow.quartz.vm.job;

import com.jcraft.jsch.JSchException;
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
import tpiskorski.machinator.flow.parser.*;
import tpiskorski.machinator.flow.quartz.service.StartVmService;
import tpiskorski.machinator.flow.ssh.ScpClient;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VmMoveJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmMoveJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;
    private final ConfigService configService;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();
    private ProgressCommandsInterpreter progressCommandsInterpreter = new ProgressCommandsInterpreter();
    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private ShowVmStateParser showVmStateParser = new ShowVmStateParser();

    private ScpClient scpClient = new ScpClient();
    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    private StartVmService startVmService;

    @Autowired
    public VmMoveJob(CommandExecutor commandExecutor, CommandFactory commandFactory, ConfigService configService) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
        this.configService = configService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        Server source = vm.getServer();
        Server destination = (Server) mergedJobDataMap.get("destination");

        if (source.getServerType() == ServerType.LOCAL && destination.getServerType() == ServerType.REMOTE) {
            moveFromLocalToRemote(vm, source, destination);
        } else if (source.getServerType() == ServerType.REMOTE && destination.getServerType() == ServerType.LOCAL) {
            moveFromRemoteToLocal(vm, source, destination);
        } else if (source.getServerType() == ServerType.REMOTE && destination.getServerType() == ServerType.REMOTE) {
            moveBetweenRemotes(vm, source, destination);
        }
    }

    private void moveFromLocalToRemote(VirtualMachine vm, Server source, Server destination) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(source, vm);
            exportVm(source, vm);

            File backupLocation = new File(configService.getConfig().getBackupLocation() + "/tmp");
            backupLocation.mkdirs();

            String tempFileName = "temp_" + vm.getServerAddress() + "_" + vm.getVmName();
            String tempFilePath = backupLocation + "/" + tempFileName;

            exportVm(vm, source, tempFilePath);

            copyFromLocalToRemote(destination, backupLocation, tempFileName);
            importVm(destination, tempFileName);

            vm.setServer(destination);
            startVmService.start(vm);

            deleteVm(vm, source);
            cleanup(destination, tempFilePath);
        } catch (IOException | InterruptedException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void moveFromRemoteToLocal(VirtualMachine vm, Server source, Server destination) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(source, vm);

            File backupLocation = new File(configService.getConfig().getBackupLocation() + "/tmp");
            backupLocation.mkdirs();

            String tempFileName = "temp_" + vm.getServerAddress() + "_" + vm.getVmName();

            exportVm(vm, source, tempFileName);
            copyFromRemoteToLocal(source, backupLocation, tempFileName);
            importVm(destination, backupLocation.toString() + "/" + tempFileName);

            startVmService.start(vm);
            vm.setServer(destination);

            cleanup(source, "~/" + tempFileName + ".ova");
        } catch (IOException | InterruptedException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void copyFromRemoteToLocal(Server source, File backupLocation, String tempFileName) throws JSchException, IOException {
        RemoteContext remoteContext = RemoteContext.of(source);
        scpClient.copyRemoteToLocal(remoteContext, "~", backupLocation.toString(), tempFileName + ".ova");
    }

    private void moveBetweenRemotes(VirtualMachine vm, Server source, Server destination) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(source, vm);

            File backupLocation = new File(configService.getConfig().getBackupLocation() + "/tmp");
            backupLocation.mkdirs();

            String tempFileName = "temp_" + vm.getServerAddress() + "_" + vm.getVmName();
            String tempFilePath = "~" + "/" + tempFileName;

            exportVm(source, vm);
            copyFromRemoteToLocal(source, backupLocation, tempFileName + ".ova");
            copyFromLocalToRemote(destination, backupLocation, tempFileName + ".ova");

            importVm(destination, tempFileName);
            vm.setServer(destination);
            startVmService.start(vm);
            deleteVm(vm, source);
            cleanup(destination, "~/" + tempFilePath + ".ova");
        } catch (IOException | InterruptedException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void powerOffIfRunning(Server source, VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext infoVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .executeOn(source)
            .build();

        ExecutionContext turnOff = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.TURN_OFF, vm.getVmName()))
            .executeOn(source)
            .build();

        CommandResult result = commandExecutor.execute(infoVm);
        ShowVmInfoUpdate update = showVmInfoParser.parse(result);

        if (update.getState() != VirtualMachineState.POWEROFF) {
            result = commandExecutor.execute(turnOff);

            if (!progressCommandsInterpreter.isSuccess(result)) {
                throw new JobExecutionException(result.getError());
            }

            pollExecutor.pollExecute(() -> showVmStateParser.parse(commandExecutor.execute(infoVm)) == VirtualMachineState.POWEROFF);
        }
    }

    private void exportVm(VirtualMachine vm, Server source, String tempFilePath) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(source)
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, tempFilePath, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(exportVm);
        if (!exportVmResultInterpreter.isSuccess(result)) {
            LOGGER.error("Backup job failed");
            throw new JobExecutionException(result.getError());
        }
    }

    private void copyFromLocalToRemote(Server destination, File backupLocation, String tempFileName) throws JSchException, IOException {
        RemoteContext remoteContext = RemoteContext.of(destination);
        scpClient.copyLocalToRemote(remoteContext, backupLocation.toString(), "~", tempFileName + ".ova");
    }

    private void cleanup(Server destination, String tempFilePath) throws IOException, InterruptedException {
        ExecutionContext cleanup = ExecutionContext.builder()
            .executeOn(destination)
            .command(commandFactory.makeWithArgs(BaseCommand.RM_FILES, "~/" + tempFilePath + ".ova"))
            .build();
        CommandResult execute2 = commandExecutor.execute(cleanup);

        Files.delete(Paths.get(tempFilePath + ".ova"));
    }

    private void deleteVm(VirtualMachine vm, Server source) throws IOException, InterruptedException, JobExecutionException {
        ExecutionContext infoVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId()))
            .executeOn(source)
            .build();

        ExecutionContext deleteVm = ExecutionContext.builder()
            .executeOn(source)
            .command(commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(deleteVm);
        if (!progressCommandsInterpreter.isSuccess(result)) {
            ShowVmInfoUpdate update = showVmInfoParser.parse(commandExecutor.execute(infoVm));
            vm.setState(update.getState());
            throw new JobExecutionException(result.getError());
        }
    }

    private void importVm(Server destination, String tempFileName) throws IOException, InterruptedException {
        ExecutionContext importVm = ExecutionContext.builder()
            .command(commandFactory.makeWithArgs(BaseCommand.IMPORT_VM, tempFileName))
            .executeOn(destination)
            .build();

        CommandResult execute = commandExecutor.execute(importVm);
    }

    private void exportVm(Server source, VirtualMachine vm) throws JobExecutionException, IOException, InterruptedException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/tmp");
        backupLocation.mkdirs();

        String tempFileName = "temp_" + vm.getServerAddress() + "_" + vm.getVmName();
        String tempFilePath = backupLocation + "/" + tempFileName;

        ExecutionContext exportVm = ExecutionContext.builder()
            .executeOn(source)
            .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, tempFilePath, vm.getVmName()))
            .build();

        CommandResult result = commandExecutor.execute(exportVm);
        if (!exportVmResultInterpreter.isSuccess(result)) {
            LOGGER.error("Backup job failed");
            throw new JobExecutionException(result.getError());
        }
    }
}
