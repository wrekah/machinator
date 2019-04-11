package tpiskorski.machinator.quartz.server;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

@Profile("!demo")
@Controller
public class ServerRefreshScheduler implements InitializingBean, ServerRefresh {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRefreshScheduler.class);

    private final Scheduler scheduler;
    private final ServerRefreshJobListener serverRefreshJobListener;

    @Autowired public ServerRefreshScheduler(Scheduler scheduler, ServerRefreshJobListener serverRefreshJobListener) {
        this.scheduler = scheduler;
        this.serverRefreshJobListener = serverRefreshJobListener;
    }

    private void schedule() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(ServerRefreshJob.class)
            .withIdentity(ServerRefreshJob.NAME)
            .storeDurably()
            .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.getKey().getName())
            .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * ? * *"))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        LOGGER.info("Scheduled server refresh job");
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(serverRefreshJobListener);
        schedule();
    }

    @Override public void pause() {
        try {
            scheduler.pauseJob(JobKey.jobKey(ServerRefreshJob.NAME));
            LOGGER.info("Paused server refresh job");
        } catch (SchedulerException e) {
            LOGGER.info("Could not pause server refresh job", e);
        }
    }

    @Override public void resume() {
        try {
            scheduler.resumeJob(JobKey.jobKey(ServerRefreshJob.NAME));
            LOGGER.info("Resumed server refresh job");
        } catch (SchedulerException e) {
            LOGGER.info("Could not resume server refresh job", e);
        }
    }

    @Override public boolean isPaused() {
        try {
            Trigger onlyTrigger = scheduler.getTriggersOfJob(JobKey.jobKey(ServerRefreshJob.NAME)).get(0);
            Trigger.TriggerState onlyTriggerState = scheduler.getTriggerState(onlyTrigger.getKey());
            return onlyTriggerState == Trigger.TriggerState.PAUSED;
        } catch (SchedulerException e) {
            LOGGER.info("Could not get server refresh job state", e);
            return false;
        }
    }
}
