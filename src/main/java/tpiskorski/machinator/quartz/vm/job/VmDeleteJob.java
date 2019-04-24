package tpiskorski.machinator.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.server.ServerType;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineService;

import java.io.IOException;
import java.util.List;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final RemoteCommandExecutor remoteCommandExecutor;
    private final CommandFactory commandFactory;

    @Autowired private VirtualMachineService virtualMachineService;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();
    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    @Autowired
    public VmDeleteJob(LocalMachineCommandExecutor localMachineCommandExecutor, RemoteCommandExecutor remoteCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.remoteCommandExecutor = remoteCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        //todo error handling
        Command deleteVmCommand = commandFactory.makeWithArgs(BaseCommand.DELETE_VM, vm.getVmName());
        Command listAllVmsCommand = commandFactory.makeWithArgs(BaseCommand.LIST_ALL_VMS, vm.getId());

        try {
            CommandResult result;
            if (vm.getServer().getServerType() == ServerType.LOCAL) {
                result = localMachineCommandExecutor.execute(deleteVmCommand);
                result = localMachineCommandExecutor.execute(listAllVmsCommand);
                List<VirtualMachine> vms = simpleVmParser.parse(result);
                if (!vms.contains(vm)) {
                    virtualMachineService.remove(vm);
                } else {
                    throw new JobExecutionException("Could not delete vm");
                }

                ShowVmInfoUpdate update = showVmInfoParser.parse(result);
                vm.setState(update.getState());
            } else {
                RemoteContext remoteContext = RemoteContext.of(vm.getServer());
                result = remoteCommandExecutor.execute(deleteVmCommand, remoteContext);
                result = remoteCommandExecutor.execute(listAllVmsCommand, remoteContext);
                List<VirtualMachine> vms = simpleVmParser.parse(result);
                if (!vms.contains(vm)) {
                    virtualMachineService.remove(vm);
                } else {
                    throw new JobExecutionException("Could not delete vm");
                }

            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmDeleteJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
