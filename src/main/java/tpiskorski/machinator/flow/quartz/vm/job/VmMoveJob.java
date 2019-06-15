package tpiskorski.machinator.flow.quartz.vm.job;

import com.jcraft.jsch.JSchException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.flow.quartz.service.*;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;

public class VmMoveJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmMoveJob.class);

    @Autowired private VmManipulator vmManipulator;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private CleanupService cleanupService;
    @Autowired private ExportVmService exportVmService;
    @Autowired private VmImporter vmImporter;
    @Autowired private CopyService copyService;
    @Autowired private BackupService backupService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private VmLister vmLister;

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        Server source = vm.getServer();
        Server destination = (Server) mergedJobDataMap.get("destination");

        VirtualMachine placeholder = VirtualMachine.placeholderFor(vm, destination);
        virtualMachineService.put(placeholder);

        placeholder.lock();
        source.lock();
        destination.lock();
        try {

            if (source.getServerType() == ServerType.LOCAL && destination.getServerType() == ServerType.REMOTE) {
                moveFromLocalToRemote(vm, source, destination);
            } else if (source.getServerType() == ServerType.REMOTE && destination.getServerType() == ServerType.LOCAL) {
                moveFromRemoteToLocal(vm, source, destination);
            } else if (source.getServerType() == ServerType.REMOTE && destination.getServerType() == ServerType.REMOTE) {
                moveBetweenRemotes(vm, source, destination);
            }
        } finally {
            virtualMachineService.remove(placeholder);
            placeholder.unlock();
            source.unlock();
            destination.unlock();
        }
    }

    private void moveFromLocalToRemote(VirtualMachine vm, Server local, Server remote) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(vm);

            String localTemporaryFilePath = backupService.getLocalTemporaryFilePath(vm);
            String remoteTemporaryFilePath = backupService.getRemoteTemporaryFilePath(vm);

            try {
                cleanupService.cleanup(remote, remoteTemporaryFilePath);
                cleanupService.cleanup(local, localTemporaryFilePath);

                exportVmService.exportVm(local, localTemporaryFilePath, vm.getVmName());

                copyService.copyLocalToRemote(remote, localTemporaryFilePath, remoteTemporaryFilePath);
                vmImporter.importVm(remote, remoteTemporaryFilePath);

                vmManipulator.remove(local, vm.getVmName());

                vm.setServer(remote);
                String newId = getNewId(remote, vm.getVmName());
                vm.setId(newId);

                vmManipulator.start(vm);
                virtualMachineService.persist();
            } finally {
                cleanupService.cleanup(remote, remoteTemporaryFilePath);
                cleanupService.cleanup(local, localTemporaryFilePath);
            }
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

            String remoteTemporaryFilePath = backupService.getRemoteTemporaryFilePath(vm);
            String localTemporaryFilePath = backupService.getLocalTemporaryFilePath(vm);

            try {
                cleanupService.cleanup(remote, remoteTemporaryFilePath);
                cleanupService.cleanup(local, localTemporaryFilePath);

                exportVmService.exportVm(remote, remoteTemporaryFilePath, vm.getVmName());
                copyService.copyRemoteToLocal(remote, remoteTemporaryFilePath, localTemporaryFilePath);
                vmImporter.importVm(local, localTemporaryFilePath);

                vmManipulator.remove(remote, vm.getVmName());

                vm.setServer(local);
                String newId = getNewId(local, vm.getVmName());
                vm.setId(newId);

                vmManipulator.start(vm);
                virtualMachineService.persist();
            } finally {
                cleanupService.cleanup(remote, remoteTemporaryFilePath);
                cleanupService.cleanup(local, localTemporaryFilePath);
            }
        } catch (IOException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private String getNewId(Server server, String vmName) {
        VirtualMachine virtualMachine = vmLister.simpleList(server).stream()
            .filter(it -> it.getVmName().equals(vmName))
            .findFirst()
            .orElseThrow();

        return virtualMachine.getId();
    }

    private void moveBetweenRemotes(VirtualMachine vm, Server fromRemote, Server toRemote) throws JobExecutionException {
        vm.lock();
        try {
            powerOffIfRunning(vm);

            String remoteTemporaryFilePath = backupService.getRemoteTemporaryFilePath(vm);
            String localTemporaryFilePath = backupService.getLocalTemporaryFilePath(vm);

            try {
                cleanupService.cleanup(fromRemote, remoteTemporaryFilePath);
                cleanupService.cleanup(Server.local(), localTemporaryFilePath);
                cleanupService.cleanup(toRemote, remoteTemporaryFilePath);

                exportVmService.exportVm(fromRemote, remoteTemporaryFilePath, vm.getVmName());
                copyService.copyRemoteToLocal(fromRemote, remoteTemporaryFilePath, localTemporaryFilePath);

                copyService.copyLocalToRemote(toRemote, localTemporaryFilePath, remoteTemporaryFilePath);
                vmImporter.importVm(toRemote, remoteTemporaryFilePath);

                vmManipulator.remove(fromRemote, vm.getVmName());
                vm.setServer(toRemote);
                String newId = getNewId(toRemote, vm.getVmName());
                vm.setId(newId);

                vmManipulator.start(vm);
                virtualMachineService.persist();
            } finally {
                cleanupService.cleanup(fromRemote, remoteTemporaryFilePath);
                cleanupService.cleanup(Server.local(), localTemporaryFilePath);
                cleanupService.cleanup(toRemote, remoteTemporaryFilePath);
            }
        } catch (IOException | JSchException e) {
            LOGGER.error("Backup job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void powerOffIfRunning(VirtualMachine vm) {
        if (vmInfoService.state(vm) != VirtualMachineState.POWEROFF) {
            vmManipulator.powerOff(vm);

            vmInfoService.pollState(vm, VirtualMachineState.POWEROFF);
        }
    }
}
