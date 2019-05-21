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
import tpiskorski.machinator.flow.quartz.service.StartVmService;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

@Component
public class VmTurnOnJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmTurnOnJob.class);

    private final StartVmService startVmService;
    private final VmInfoService vmInfoService;

    private PollExecutor pollExecutor = new PollExecutor();

    @Autowired
    public VmTurnOnJob(StartVmService startVmService, VmInfoService vmInfoService) {
        this.startVmService = startVmService;
        this.vmInfoService = vmInfoService;
    }

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        vm.lock();
        try {
            startVmService.start(vm);
            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.RUNNING);
            vm.setState(VirtualMachineState.RUNNING);
        } finally {
            vm.unlock();
        }
    }
}
