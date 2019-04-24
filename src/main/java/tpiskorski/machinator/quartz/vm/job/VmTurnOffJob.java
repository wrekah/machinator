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
import tpiskorski.machinator.core.vm.VirtualMachineState;

import java.io.IOException;

@Component
public class VmTurnOffJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOffJob.class);

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final RemoteCommandExecutor remoteCommandExecutor;
    private final CommandFactory commandFactory;

    private ShowVmInfoParser showVmInfoParser = new ShowVmInfoParser();


    @Autowired
    public VmTurnOffJob(LocalMachineCommandExecutor localMachineCommandExecutor, RemoteCommandExecutor remoteCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.remoteCommandExecutor = remoteCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());
        vm.lock();
        //todo error handling

        Command turnOffVmCommand = commandFactory.makeWithArgs(BaseCommand.TURN_OFF, vm.getVmName());
        Command infoVmCommand = commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, vm.getId());

        try {

            CommandResult result;
            if (vm.getServer().getServerType() == ServerType.LOCAL) {
                result = localMachineCommandExecutor.execute(turnOffVmCommand);
                result = localMachineCommandExecutor.execute(infoVmCommand);
                ShowVmInfoUpdate update = showVmInfoParser.parse(result);
                vm.setState(update.getState());
            } else {
                RemoteContext remoteContext = RemoteContext.of(vm.getServer());
                result = remoteCommandExecutor.execute(turnOffVmCommand, remoteContext);
                result = remoteCommandExecutor.execute(infoVmCommand, remoteContext);
                ShowVmInfoUpdate update = showVmInfoParser.parse(result);
                vm.setState(update.getState());
            }

            vm.unlock();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmTurnOffJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
