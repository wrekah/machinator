package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.model.watchdog.Watchdog;

@Service
public class WatchdogScheduler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogScheduler.class);

    private final Scheduler scheduler;
    private final WatchdogJobListener watchdogJobListener;

    public WatchdogScheduler(Scheduler scheduler, WatchdogJobListener watchdogJobListener) {
        this.scheduler = scheduler;
        this.watchdogJobListener = watchdogJobListener;
    }

    public void schedule(Watchdog watchdog) {
        JobKey jobKey = JobKey.jobKey(watchdog.toString(), "watchdogs");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("watchdog", watchdog);

        JobDetail job = JobBuilder.newJob(WatchdogJob.class).withIdentity(jobKey).storeDurably()
            .usingJobData(jobDataMap)
            .build();

        try {
            scheduler.addJob(job, true);
            scheduler.triggerJob(JobKey.jobKey(watchdog.toString(), "watchdogs"));
            LOGGER.info("Scheduled watchdog {}", watchdog);
        } catch (SchedulerException e) {
            LOGGER.error("Could not add job to scheduler", e);
        }
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(watchdogJobListener);
    }
}
