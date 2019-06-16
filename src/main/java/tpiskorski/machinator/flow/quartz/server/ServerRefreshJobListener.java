package tpiskorski.machinator.flow.quartz.server;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.model.job.JobStatus;
import tpiskorski.machinator.model.job.JobType;

import java.time.LocalDateTime;

@Component
public class ServerRefreshJobListener implements JobListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRefreshJobListener.class);

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
            LOGGER.debug("Server refresh job is scheduled for execution {}", context.getJobDetail());
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (isServerRefreshJob(context)) {
            LOGGER.warn("Server refresh job was vetoed {}", context.getJobDetail());
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isServerRefreshJob(context)) {
            if (jobException != null) {
                LOGGER.error("Server refresh job failed  {}", context.getJobDetail());

                Job job = createJob(context, jobException);
                jobService.add(job);
            }
        }
    }

    private Job createJob(JobExecutionContext context, JobExecutionException jobException) {
        JobKey key = context.getJobDetail().getKey();
        Job job = new Job(key.getName());

        job.setJobType(JobType.SERVER_REFRESH);
        job.setDescription("Regular server refresh");

        LocalDateTime now = LocalDateTime.now();
        job.setStartTime(now);
        job.setEndTime(now);

        job.setStatus(JobStatus.FAILED);
        SchedulerException cause = (SchedulerException) jobException.getCause();
        String unwrappedMessage = cause.getUnderlyingException().getMessage();
        job.setErrorCause(unwrappedMessage);

        return job;
    }

    private boolean isServerRefreshJob(JobExecutionContext context) {
        return context.getJobDetail().getJobClass().equals(ServerRefreshJob.class);
    }
}
