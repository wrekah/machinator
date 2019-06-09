package tpiskorski.machinator.lifecycle.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;

@Profile("!dev")
@Service
public class StatePersistScheduler implements PersistScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatePersistScheduler.class);

    private final Scheduler scheduler;

    public StatePersistScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override public void schedulePersistence(PersistenceType persistenceType) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(StatePersistJob.PERSISTENCE_TYPE_KEY, persistenceType);

        JobDetail jobDetail = JobBuilder.newJob(StatePersistJob.class)
            .usingJobData(jobDataMap)
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
}
