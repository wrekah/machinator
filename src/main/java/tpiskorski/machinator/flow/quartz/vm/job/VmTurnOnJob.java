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
public class VmTurnOnJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOnJob.class);

    private final VmManipulator vmManipulator;
    private final VmInfoService vmInfoService;

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    public VmTurnOnJob(VmManipulator vmManipulator, VmInfoService vmInfoService) {
        this.vmManipulator = vmManipulator;
        this.vmInfoService = vmInfoService;
    }

    @Override protected void executeInternal(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        LOGGER.info("Started vm turn on job for {}", vm);

        vm.lock();
        try {
            vmManipulator.start(vm);
            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.RUNNING);
            vm.setState(VirtualMachineState.RUNNING);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Finished vm turn on job for {}", vm);
    }
}
