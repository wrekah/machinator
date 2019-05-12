package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.model.watchdog.Watchdog;

@Service
public class WatchdogScheduler implements InitializingBean {

    private final Scheduler scheduler;
    private final WatchdogJobListener watchdogJobListener;

    public WatchdogScheduler(Scheduler scheduler, WatchdogJobListener watchdogJobListener) {
        this.scheduler = scheduler;
        this.watchdogJobListener = watchdogJobListener;
    }

    public void schedule(Watchdog watchdog) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(watchdog.toString(), "watchdogs");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("watchdog", watchdog);

        JobDetail job = JobBuilder.newJob(WatchdogJob.class).withIdentity(jobKey).storeDurably()
            .usingJobData(jobDataMap)
            .build();
        scheduler.addJob(job, true);
        scheduler.triggerJob(JobKey.jobKey(watchdog.toString(), "watchdogs"));
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(watchdogJobListener);
    }
}
