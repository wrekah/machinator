package tpiskorski.machinator.flow.quartz.server;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.model.job.JobStatus;
import tpiskorski.machinator.model.job.JobType;

import java.time.LocalDateTime;

@Component
public class ServerRefreshJobListener implements JobListener {

    private static final String LISTENER_NAME = "ServerRefreshListener";

    private final JobService jobService;

    @Autowired public ServerRefreshJobListener(JobService jobService) {
        this.jobService = jobService;
    }

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        if (isServerRefreshJob(context)) {
            JobKey key = context.getJobDetail().getKey();
            Job job = new Job(key.getName());

            job.setJobType(JobType.SERVER_REFRESH);
            job.setDescription("Regular server refresh");
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (isServerRefreshJob(context)) {
            Job job = jobService.getLastServerRefreshJob();
            job.setStatus(JobStatus.CANCELLED);
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isServerRefreshJob(context)) {
            Job job = jobService.getLastServerRefreshJob();
            job.setEndTime(LocalDateTime.now());
            if (jobException == null) {
                job.setStatus(JobStatus.COMPLETED);
            } else {
                job.setStatus(JobStatus.FAILED);
                job.setErrorCause(jobException.getMessage());
            }
        }
    }

    private boolean isServerRefreshJob(JobExecutionContext context) {
        return context.getJobDetail().getJobClass().equals(ServerRefreshJob.class);
    }
}
