package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.quartz.service.VmManipulator;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

@Component
public class VmResetJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmResetJob.class);

    private final VmManipulator vmManipulator;

    @Autowired
    public VmResetJob(VmManipulator vmManipulator) {
        this.vmManipulator = vmManipulator;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        LOGGER.info("Started vm reset job for {}", vm);

        vm.lock();
        try {
            vmManipulator.resetVm(vm);
            vm.setState(VirtualMachineState.RUNNING_RECENTLY_RESET);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Finished vm reset job for {}", vm);
    }
}
