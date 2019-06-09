package tpiskorski.machinator.flow.quartz.server;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.config.Config;
import tpiskorski.machinator.config.ConfigService;

@Controller
public class ServerRefreshScheduler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRefreshScheduler.class);

    private final Scheduler scheduler;
    private final ServerRefreshJobListener serverRefreshJobListener;
    private final ConfigService configService;

    @Autowired
    public ServerRefreshScheduler(Scheduler scheduler, ServerRefreshJobListener serverRefreshJobListener, ConfigService configService) {
        this.scheduler = scheduler;
        this.serverRefreshJobListener = serverRefreshJobListener;
        this.configService = configService;
    }

    private void schedule() throws SchedulerException {
        int pollInterval = configService.getConfig().getPollInterval();

        JobDetail jobDetail = buildJobDetail();

        SimpleTrigger trigger = buildTrigger(pollInterval, jobDetail);

        scheduler.scheduleJob(jobDetail, trigger);
        LOGGER.info("Scheduled server refresh job with interval {} sec", pollInterval);
    }

    private SimpleTrigger buildTrigger(int pollInterval, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
            .withIdentity(TriggerKey.triggerKey(ServerRefreshJob.TRIGGER_NAME))
            .forJob(jobDetail)
            .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(pollInterval))
            .build();
    }

    @Override public void afterPropertiesSet() throws Exception {
        configService.addPropertyChangeListener(evt -> {
            Config oldConfig = ((Config) evt.getOldValue());
            Config newConfig = (Config) evt.getNewValue();

            if (oldConfig != null && newConfig != null) {
                if (oldConfig.getPollInterval() != newConfig.getPollInterval()) {
                    LOGGER.info("Rescheduling server refresh to {} sec", newConfig.getPollInterval());
                    reschedule();
                }
            }
        });

        scheduler.getListenerManager().addJobListener(serverRefreshJobListener);
        schedule();
    }

    public void reschedule() {
        try {
            int pollInterval = configService.getConfig().getPollInterval();

            JobDetail jobDetail = buildJobDetail();
            SimpleTrigger newTrigger = buildTrigger(pollInterval, jobDetail);

            scheduler.rescheduleJob(TriggerKey.triggerKey(ServerRefreshJob.TRIGGER_NAME), newTrigger);
        } catch (SchedulerException e) {
            LOGGER.info("Could not pause server refresh job", e);
        }
    }

    private JobDetail buildJobDetail() {
        return JobBuilder.newJob(ServerRefreshJob.class)
            .withIdentity(ServerRefreshJob.JOB_NAME)
            .storeDurably()
            .build();
    }

    public void pause() {
        try {
            scheduler.pauseJob(JobKey.jobKey(ServerRefreshJob.JOB_NAME));
            LOGGER.info("Paused server refresh job");
        } catch (SchedulerException e) {
            LOGGER.info("Could not pause server refresh job", e);
        }
    }

    public void resume() {
        try {
            scheduler.resumeJob(JobKey.jobKey(ServerRefreshJob.JOB_NAME));
            LOGGER.info("Resumed server refresh job");
        } catch (SchedulerException e) {
            LOGGER.info("Could not resume server refresh job", e);
        }
    }

    public boolean isPaused() {
        try {
            Trigger onlyTrigger = scheduler.getTriggersOfJob(JobKey.jobKey(ServerRefreshJob.JOB_NAME)).get(0);
            Trigger.TriggerState onlyTriggerState = scheduler.getTriggerState(onlyTrigger.getKey());
            return onlyTriggerState == Trigger.TriggerState.PAUSED;
        } catch (SchedulerException e) {
            LOGGER.info("Could not get server refresh job state", e);
            return false;
        }
    }
}
