package tpiskorski.machinator.quartz.vm;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.core.job.Job;
import tpiskorski.machinator.core.job.JobService;
import tpiskorski.machinator.core.job.JobStatus;
import tpiskorski.machinator.core.job.JobType;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.quartz.vm.job.VmDeleteJob;

import java.time.LocalDateTime;

@Component
public class VmDeleteJobListener implements JobListener {

    private static final String LISTENER_NAME = "VmActionJobListener";

    private final JobService jobService;

    @Autowired public VmDeleteJobListener(JobService jobService) {
        this.jobService = jobService;
    }

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        if (isVmActionJob(context)) {

            JobKey key = context.getJobDetail().getKey();

            JobDataMap map = context.getMergedJobDataMap();
            VirtualMachine backupDefinition = (VirtualMachine) map.get("vm");

            Job job = new Job(key.getName());

            job.setJobType(JobType.VM_ACTION);
            job.setDescription(backupDefinition.getVmName());
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isVmActionJob(context)) {

            JobKey key = context.getJobDetail().getKey();

            Job job = jobService.getLast(key.getName());
            job.setEndTime(LocalDateTime.now());
            if (jobException == null) {
                job.setStatus(JobStatus.COMPLETED);
            } else {
                job.setStatus(JobStatus.FAILED);
                job.setErrorCause(jobException.getMessage());
            }
        }
    }

    private boolean isVmActionJob(JobExecutionContext context) {
        return context.getJobDetail().getJobClass().equals(VmDeleteJob.class);
    }
}
