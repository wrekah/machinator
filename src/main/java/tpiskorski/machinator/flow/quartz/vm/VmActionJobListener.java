package tpiskorski.machinator.flow.quartz.vm;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.quartz.vm.job.*;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.model.job.JobStatus;
import tpiskorski.machinator.model.job.JobType;

import java.time.LocalDateTime;

@Component
public class VmActionJobListener implements JobListener {

    private static final String LISTENER_NAME = "VmActionJobListener";

    private final JobService jobService;

    @Autowired public VmActionJobListener(JobService jobService) {
        this.jobService = jobService;
    }

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        if (isVmActionJob(context)) {
            JobKey key = context.getJobDetail().getKey();
            Job job = new Job(key.getName());

            job.setJobType(JobType.VM_ACTION);
            job.setDescription(context.getJobDetail().getJobClass().getName());
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (isVmActionJob(context)) {
            Job job = jobService.getLastServerRefreshJob();
            jobService.update(job, JobStatus.CANCELLED);
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isVmActionJob(context)) {
            JobKey key = context.getJobDetail().getKey();
            Job job = jobService.get(key.getName());
            jobService.updateEndTime(job, LocalDateTime.now());
            if (jobException == null) {
                jobService.update(job, JobStatus.COMPLETED);
            } else {
                jobService.update(job, JobStatus.FAILED);

                String unwrappedMessage = ((SchedulerException) jobException.getCause()).getUnderlyingException().getMessage();
                job.setErrorCause(unwrappedMessage);
            }
        }
    }

    private boolean isVmActionJob(JobExecutionContext context) {
        return context.getJobDetail().getJobClass().equals(VmDeleteJob.class) ||
            context.getJobDetail().getJobClass().equals(VmPowerOffJob.class) ||
            context.getJobDetail().getJobClass().equals(VmResetJob.class) ||
            context.getJobDetail().getJobClass().equals(VmTurnOnJob.class) ||
            context.getJobDetail().getJobClass().equals(VmMoveJob.class) ||
            context.getJobDetail().getJobClass().equals(VmTurnOffJob.class);
    }
}
