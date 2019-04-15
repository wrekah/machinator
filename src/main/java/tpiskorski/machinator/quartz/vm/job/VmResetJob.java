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
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineState;

import java.io.IOException;

@Component
public class VmResetJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmResetJob.class);

    private final LocalMachineCommandExecutor localMachineCommandExecutor;
    private final CommandFactory commandFactory;

    private VmResetResultInterpreter vmResetResultInterpreter = new VmResetResultInterpreter();

    @Autowired
    public VmResetJob(LocalMachineCommandExecutor localMachineCommandExecutor, CommandFactory commandFactory) {
        this.localMachineCommandExecutor = localMachineCommandExecutor;
        this.commandFactory = commandFactory;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        vm.lock();
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        Command command = commandFactory.makeWithArgs(BaseCommand.RESET_VM, vm.getVmName());

        try {
            CommandResult result = localMachineCommandExecutor.execute(command);
            if(vmResetResultInterpreter.isSuccess(result)){
                vm.setState(VirtualMachineState.RUNNING_RECENTLY_RESET);
                vm.unlock();
            }else{
                vm.setState(VirtualMachineState.POWEROFF);
                vm.unlock();
                throw new JobExecutionException("Fail");
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmResetJob job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
