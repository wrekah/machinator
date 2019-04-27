package tpiskorski.machinator.quartz.vm.job;

import com.jcraft.jsch.JSchException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tpiskorski.machinator.action.CommandExecutor;
import tpiskorski.machinator.action.ExecutionContext;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerType;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineState;
import tpiskorski.machinator.quartz.backup.ExportVmResultInterpreter;
import tpiskorski.machinator.quartz.backup.ScpClient;

import java.io.IOException;

public class VmMoveJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmMoveJob.class);

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private ExportVmResultInterpreter exportVmResultInterpreter = new ExportVmResultInterpreter();
    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private ScpClient scpClient = new ScpClient();

    @Autowired
    public VmMoveJob(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        Server destination = (Server) mergedJobDataMap.get("destination");

        if (vm.getServer().getServerType() == ServerType.LOCAL) {
            moveFromLocalToRemote(vm, destination);
        } else {
            moveFromRemoteToLocal(vm, destination);
        }
    }

    private void moveFromLocalToRemote(VirtualMachine vm, Server destination) throws JobExecutionException {
        try {

            Command turnOffVmCommand = commandFactory.makeWithArgs(BaseCommand.TURN_OFF, vm.getVmName());
            Command infoVmCommand = commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId());

            vm.setState(VirtualMachineState.COMMAND_IN_PROGRESS);

            ExecutionContext turnOff = ExecutionContext.builder()
                .command(turnOffVmCommand)
                .executeOn(vm.getServer())
                .build();

            ExecutionContext infoVm = ExecutionContext.builder()
                .command(infoVmCommand)
                .executeOn(vm.getServer())
                .build();

            commandExecutor.execute(turnOff);

            CommandResult result = commandExecutor.execute(infoVm);
            ShowVmInfoUpdate update = showVmInfoParser.parse(result);
            if (update.getState() != VirtualMachineState.POWEROFF) {
                throw new JobExecutionException("Could not poweroff vm");
            }

            RemoteContext remoteContext = RemoteContext.of(destination);

            ExecutionContext exportVm = ExecutionContext.builder()
                .executeOn(vm.getServer())
                .command(commandFactory.makeWithArgs(BaseCommand.EXPORT_VM, "/tmp/moveVmTempFile", vm.getVmName()))
                .build();

            try {
                result = commandExecutor.execute(exportVm);
                if (!exportVmResultInterpreter.isSuccess(result)) {
                    LOGGER.error("Backup job failed");
                    throw new JobExecutionException(result.getError());
                }
                scpClient.copyLocalToRemote(remoteContext, "/tmp", "/tmp", "moveVmTempFile.ova");

                ExecutionContext importVm = ExecutionContext.builder()
                    .command(commandFactory.makeWithArgs(BaseCommand.IMPORT_VM, "/tmp/moveVmTempFile"))
                    .executeOn(destination)
                    .build();

                ExecutionContext startVm = ExecutionContext.builder()
                    .command(commandFactory.makeWithArgs(BaseCommand.START_VM, vm.getVmName()))
                    .executeOn(destination)
                    .build();

                commandExecutor.execute(importVm);
                commandExecutor.execute(startVm);
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Backup job failed", e);
                throw new JobExecutionException(e);
            }
        } catch (IOException | InterruptedException | JSchException e) {
            throw new JobExecutionException(e);
        }
    }

    private void moveFromRemoteToLocal(VirtualMachine vm, Server destination) {

    }
}
