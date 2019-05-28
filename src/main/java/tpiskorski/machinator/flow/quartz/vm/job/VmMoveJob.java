package tpiskorski.machinator.flow.quartz.vm.job;

import com.jcraft.jsch.JSchException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.*;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;

public class VmMoveJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmMoveJob.class);

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired private VmManipulator vmManipulator;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;
    @Autowired private VmImporter vmImporter;
    @Autowired private CopyService copyService;
    @Autowired private BackupService backupService;

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

    private void moveFromLocalToRemote(VirtualMachine vm, Server local, Server remote) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(vm);

            String temporaryFilePath = backupService.getTemporaryFilePath(vm);
            exportVmService.exportVm(local, temporaryFilePath, vm.getVmName());
            copyService.copyLocalToRemote(remote, temporaryFilePath, temporaryFilePath);
            vmImporter.importVm(remote, temporaryFilePath);

            vmManipulator.remove(local, vm.getVmName());

            vm.setServer(remote);
            vmManipulator.start(vm);

            cleanupService.cleanup(remote, temporaryFilePath);
            cleanupService.cleanup(local, temporaryFilePath);
        } catch (IOException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void moveFromRemoteToLocal(VirtualMachine vm, Server remote, Server local) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(vm);

            String temporaryFilePath = backupService.getTemporaryFilePath(vm);
            exportVmService.exportVm(remote, temporaryFilePath, vm.getVmName());
            copyService.copyRemoteToLocal(remote, temporaryFilePath, temporaryFilePath);
            vmImporter.importVm(local, temporaryFilePath);

            vmManipulator.remove(remote, vm.getVmName());

            vm.setServer(local);
            vmManipulator.start(vm);

            cleanupService.cleanup(remote, temporaryFilePath);
            cleanupService.cleanup(local, temporaryFilePath);
        } catch (IOException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void moveBetweenRemotes(VirtualMachine vm, Server fromRemote, Server toRemote) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(vm);

            String temporaryFilePath = backupService.getTemporaryFilePath(vm);

            exportVmService.exportVm(fromRemote, temporaryFilePath, vm.getVmName());
            copyService.copyRemoteToLocal(fromRemote, temporaryFilePath, temporaryFilePath);

            copyService.copyLocalToRemote(toRemote, temporaryFilePath, temporaryFilePath);
            vmImporter.importVm(toRemote, temporaryFilePath);

            vm.setServer(toRemote);
            vmManipulator.start(vm);
            vmManipulator.remove(fromRemote, vm.getVmName());

            cleanupService.cleanup(fromRemote, temporaryFilePath);
            cleanupService.cleanup(Server.local(), temporaryFilePath);
            cleanupService.cleanup(toRemote, temporaryFilePath);
        } catch (IOException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void powerOffIfRunning(VirtualMachine vm) {
        if (vmInfoService.state(vm) != VirtualMachineState.POWEROFF) {
            vmManipulator.turnoff(vm);

            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
        }
    }
}
