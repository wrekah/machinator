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
import tpiskorski.machinator.flow.executor.RemoteContext;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.*;
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

    private final ConfigService configService;

    private ScpClient scpClient = new ScpClient();
    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired private StartVmService startVmService;
    @Autowired private PowerOffVmService powerOffVmService;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;
    @Autowired private VmImporter vmImporter;
    @Autowired private VmRemover vmRemover;

    @Autowired
    public VmMoveJob(ConfigService configService) {
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

            exportVmService.exportVm(source, tempFilePath, vm.getVmName());

            copyFromLocalToRemote(destination, backupLocation, tempFileName);
            vmImporter.importVm(destination, tempFileName);

            vm.setServer(destination);
            startVmService.start(vm);
            vmRemover.remove(source, vm.getVmName());

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

            exportVmService.exportVm(source, tempFileName, vm.getVmName());

            copyFromRemoteToLocal(source, backupLocation, tempFileName);
            vmImporter.importVm(destination, backupLocation.toString() + "/" + tempFileName);

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

            vmImporter.importVm(destination, tempFileName);

            vm.setServer(destination);
            startVmService.start(vm);
            vmRemover.remove(source, vm.getVmName());
            cleanup(destination, "~/" + tempFilePath + ".ova");
        } catch (IOException | InterruptedException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void powerOffIfRunning(Server source, VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {

        if (vmInfoService.state(vm) != VirtualMachineState.POWEROFF) {
            powerOffVmService.powerOff(vm);

            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
        }
    }

    private void copyFromLocalToRemote(Server destination, File backupLocation, String tempFileName) throws JSchException, IOException {
        RemoteContext remoteContext = RemoteContext.of(destination);
        scpClient.copyLocalToRemote(remoteContext, backupLocation.toString(), "~", tempFileName + ".ova");
    }

    private void cleanup(Server destination, String tempFilePath) throws IOException, InterruptedException {
        cleanupService.cleanup(destination, "~/" + tempFilePath + ".ova");

        Files.delete(Paths.get(tempFilePath + ".ova"));
    }

    private void exportVm(Server source, VirtualMachine vm) throws JobExecutionException, IOException, InterruptedException {
        File backupLocation = new File(configService.getConfig().getBackupLocation() + "/tmp");
        backupLocation.mkdirs();

        String tempFileName = "temp_" + vm.getServerAddress() + "_" + vm.getVmName();
        String tempFilePath = backupLocation + "/" + tempFileName;

        exportVmService.exportVm(source, tempFilePath, vm.getVmName());
    }
}
