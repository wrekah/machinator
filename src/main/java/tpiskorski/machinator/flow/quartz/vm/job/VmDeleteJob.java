package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.flow.quartz.service.VmLister;
import tpiskorski.machinator.flow.quartz.service.VmManipulator;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.util.List;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    @Autowired private VmManipulator vmManipulator;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private VmLister vmLister;

    private PollExecutor pollExecutor = new PollExecutor();

    @Override protected void executeInternal(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");

        LOGGER.info("Started vm delete job for {}", vm);

        vm.lock();
        try {
            powerOffIfRunning(vm);
            vmManipulator.remove(vm);
            checkIfDeleted(vm);
        } finally {
            vm.unlock();
        }

        LOGGER.info("Finished vm delete job for {}", vm);
    }

    private void powerOffIfRunning(VirtualMachine vm) {
        if (vmInfoService.state(vm) == VirtualMachineState.RUNNING) {
            vmManipulator.turnoff(vm);
            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
        }
    }

    private void checkIfDeleted(VirtualMachine vm) {
        List<VirtualMachine> vms = vmLister.list(vm.getServer());
        if (!vms.contains(vm)) {
            virtualMachineService.remove(vm);
        } else {
            throw new ExecutionException("Could not delete vm");
        }
    }
}
