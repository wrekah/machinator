package tpiskorski.machinator.flow.quartz.vm;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.vm.job.*;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.util.UUID;

@Service
public class VmActionScheduler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmActionScheduler.class);

    private final Scheduler scheduler;

    @Autowired private VmActionJobListener vmActionJobListener;

    @Autowired public VmActionScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public synchronized UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    public void scheduleTurnOn(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmTurnOnJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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

    public void schedulePowerOff(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmPowerOffJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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

    public void scheduleTurnOff(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmTurnOffJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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

    public void scheduleReset(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmResetJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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

    public void scheduleDelete(VirtualMachine vm) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);

        JobDetail jobDetail = JobBuilder.newJob(VmDeleteJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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

    public void scheduleMove(VirtualMachine vm, Server destination) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("vm", vm);
        jobDataMap.put("destination", destination);

        JobDetail jobDetail = JobBuilder.newJob(VmMoveJob.class)
            .withIdentity(getRandomUUID().toString(), "vmAction")
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
