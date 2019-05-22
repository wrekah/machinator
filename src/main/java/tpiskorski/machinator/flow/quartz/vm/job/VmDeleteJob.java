package tpiskorski.machinator.flow.quartz.vm.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.poll.PollExecutor;
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter;
import tpiskorski.machinator.flow.parser.SimpleVmParser;
import tpiskorski.machinator.flow.quartz.service.PowerOffVmService;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.flow.quartz.service.VmLister;
import tpiskorski.machinator.flow.quartz.service.VmRemover;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.util.List;

@Component
public class VmDeleteJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmDeleteJob.class);

    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private PowerOffVmService powerOffVmService;
    @Autowired private VmInfoService vmInfoService;
    @Autowired private VmLister vmLister;
    @Autowired private VmRemover vmRemover;

    private PollExecutor pollExecutor = new PollExecutor();

    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        VirtualMachine vm = (VirtualMachine) mergedJobDataMap.get("vm");
        LOGGER.info("Started for {}-{}", vm.getServerAddress(), vm.getVmName());

        vm.lock();
        try {
            powerOffIfRunning(vm);
            vmRemover.remove(vm);
            checkIfDeleted(vm);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("VmDeleteJob job failed", e);
            throw new JobExecutionException(e);
        } finally {
            vm.unlock();
        }
    }

    private void powerOffIfRunning(VirtualMachine vm) throws IOException, InterruptedException, JobExecutionException {
        if (vmInfoService.state(vm) == VirtualMachineState.RUNNING) {
            powerOffVmService.powerOff(vm);

            pollExecutor.pollExecute(() -> vmInfoService.state(vm) == VirtualMachineState.POWEROFF);
        }
    }

    private void checkIfDeleted(VirtualMachine vm) throws JobExecutionException {
        List<VirtualMachine> vms = vmLister.list(vm.getServer());
        if (!vms.contains(vm)) {
            virtualMachineService.remove(vm);
        } else {
            throw new JobExecutionException("Could not delete vm");
        }
    }
}
