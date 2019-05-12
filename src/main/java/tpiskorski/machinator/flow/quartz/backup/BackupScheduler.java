package tpiskorski.machinator.flow.quartz.backup;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.util.CronExpressionBuilder;
import tpiskorski.machinator.model.backup.BackupDefinition;

import java.util.HashMap;
import java.util.Map;

@Service
public class BackupScheduler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupScheduler.class);

    private final Scheduler scheduler;
    private final BackupJobListener backupJobListener;

    private CronExpressionBuilder cronExpressionBuilder = new CronExpressionBuilder();

    private Map<BackupDefinition, String> jobs = new HashMap<>();

    @Autowired
    public BackupScheduler(Scheduler scheduler, BackupJobListener backupJobListener) {
        this.scheduler = scheduler;
        this.backupJobListener = backupJobListener;
    }

    public void addTaskToScheduler(BackupDefinition backupDefinition) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("backupDefinition", backupDefinition);

        JobDetail jobDetail = JobBuilder.newJob(BackupJob.class)
            .withIdentity(backupDefinition.id(), "backups")
            .usingJobData(jobDataMap)
            .storeDurably()
            .build();

        CronTrigger trigger = buildTrigger(backupDefinition, jobDetail);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            jobs.put(backupDefinition, backupDefinition.id());
            LOGGER.info("Added job to scheduler {}", jobDetail);
        } catch (SchedulerException e) {
            LOGGER.warn("Could not add job to scheduler", e);
        }
    }

    public void removeTaskFromScheduler(BackupDefinition backupDefinition) {
        String id = jobs.get(backupDefinition);
        if (id != null) {
            try {
                scheduler.unscheduleJob(new TriggerKey(id));
                LOGGER.info("Removed job from scheduler {}", backupDefinition.id());
            } catch (SchedulerException e) {
                LOGGER.warn("Could not add job to scheduler", e);
            }
        }
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(backupJobListener);
    }

    private CronTrigger buildTrigger(BackupDefinition backupDefinition, JobDetail jobDetail) {
        String cronExpression = cronExpressionBuilder.build(backupDefinition);

        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.getKey().getName())
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();
    }

    public void triggerNow(BackupDefinition backupDefinitionToTrigger) {
        String id = jobs.get(backupDefinitionToTrigger);
        if (id != null) {
            try {
                scheduler.triggerJob(JobKey.jobKey(id, "backups"));
                LOGGER.info("Triggering job now {}", id);
            } catch (SchedulerException e) {
                LOGGER.warn("Could not add job to scheduler", e);
            }
        }
    }
}
