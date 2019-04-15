package tpiskorski.machinator.quartz.vm;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.quartz.vm.job.VmDeleteJob;

@Service
public class VmActionScheduler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmActionScheduler.class);

    private final Scheduler scheduler;

    @Autowired private VmActionJobListener vmActionJobListener;

    @Autowired public VmActionScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleTurnOn(VirtualMachine vm) {

    }

    public void schedulePowerOff(VirtualMachine vm) {

    }

    public void scheduleTurnOff(VirtualMachine vm) {

    }

    public void scheduleReset(VirtualMachine vm) {

    }

    public void scheduleDelete(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmDeleteJob.class)
            .withIdentity(vm.getVmName(), "vmAction")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.getKey().getName())
            .startNow()
            .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            LOGGER.info("Added job to scheduler {}", jobDetail);
        } catch (SchedulerException e) {
            LOGGER.warn("Could not add job to scheduler", e);
        }
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(vmActionJobListener);
    }
}
