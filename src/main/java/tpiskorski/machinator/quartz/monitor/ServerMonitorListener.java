package tpiskorski.machinator.quartz.monitor;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.core.job.Job;
import tpiskorski.machinator.core.job.JobService;
import tpiskorski.machinator.core.job.JobStatus;

import java.time.LocalDateTime;

@Component
public class ServerMonitorListener implements JobListener {

    private static final String LISTENER_NAME = "ServerMonitorListener";

    @Autowired private JobService jobService;

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        if (context.getJobDetail().getJobClass().equals(ServerRefreshJob.class)) {
            Job job = new Job(key.getName());

            job.setDescription("Server monitor");
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (context.getJobDetail().getJobClass().equals(ServerRefreshJob.class)) {
            Job job = jobService.getLastByDescription("Server monitor");
            job.setStatus(JobStatus.CANCELLED);
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (context.getJobDetail().getJobClass().equals(ServerRefreshJob.class)) {
            Job job = jobService.getLastByDescription("Server monitor");

            job.setStatus(JobStatus.COMPLETED);
        }
    }
}
