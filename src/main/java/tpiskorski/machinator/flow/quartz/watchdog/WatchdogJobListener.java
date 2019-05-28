package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.model.job.JobStatus;
import tpiskorski.machinator.model.job.JobType;
import tpiskorski.machinator.model.watchdog.Watchdog;

import java.time.LocalDateTime;

@Component
public class WatchdogJobListener implements JobListener {

    private static final String LISTENER_NAME = "WatchdogJobListener";
    @Autowired private JobService jobService;

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("watchdogs")) {

            JobDataMap map = context.getMergedJobDataMap();
            Watchdog watchdog = (Watchdog) map.get("watchdog");

            Job job = new Job(key.getName());

            job.setJobType(JobType.WATCHDOG);
            job.setDescription(watchdog.toString());
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("watchdogs")) {

            Job job = jobService.getLast(key.getName());
            job.setEndTime(LocalDateTime.now());
            if (jobException == null) {
                job.setStatus(JobStatus.COMPLETED);
            } else {
                job.setStatus(JobStatus.FAILED);
                String unwrappedMessage = ((SchedulerException) jobException.getCause()).getUnderlyingException().getMessage();
                job.setErrorCause(unwrappedMessage);
            }
        }
    }
}
