package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
public class VmPowerOffJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmPowerOffJob.class);

    private final VmInfoService vmInfoService;
    private final VmManipulator vmManipulator;

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    public VmPowerOffJob(VmInfoService vmInfoService, VmManipulator vmManipulator) {
        this.vmInfoService = vmInfoService;
        this.vmManipulator = vmManipulator;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        LOGGER.info("Started vm power off job for {}", vm);

        vm.lock();
        try {
            vmManipulator.powerOff(vm);
            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
            vm.setState(VirtualMachineState.POWEROFF);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Finished vm power off job for {}", vm);
    }
}
