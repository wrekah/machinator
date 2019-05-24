package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.flow.quartz.service.VmManipulator;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

@Component
public class VmTurnOffJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOffJob.class);

    private final VmManipulator vmManipulator;
    private final VmInfoService vmInfoService;

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    public VmTurnOffJob(VmManipulator vmManipulator, VmInfoService vmInfoService) {
        this.vmManipulator = vmManipulator;
        this.vmInfoService = vmInfoService;
    }

    @Override protected void executeInternal(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        LOGGER.info("Started vm turn off job for {}", vm);

        vm.lock();
        try {
            vmManipulator.turnoff(vm);
            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
            vm.setState(VirtualMachineState.POWEROFF);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Finished vm turn off job for {}", vm);
    }
}
