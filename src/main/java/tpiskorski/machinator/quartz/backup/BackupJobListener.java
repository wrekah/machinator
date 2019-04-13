package tpiskorski.machinator.quartz.backup;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.core.backup.BackupDefinition;
import tpiskorski.machinator.core.job.Job;
import tpiskorski.machinator.core.job.JobService;
import tpiskorski.machinator.core.job.JobStatus;

import java.time.LocalDateTime;

@Component
public class BackupJobListener implements JobListener {

    private static final String LISTENER_NAME = "BackupJobListener";
    @Autowired private JobService jobService;

    @Override public String getName() {
        return LISTENER_NAME;
    }

    @Override public void jobToBeExecuted(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("backups")) {

            JobDataMap map = context.getMergedJobDataMap();
            BackupDefinition backupDefinition = (BackupDefinition) map.get("backupDefinition");

            Job job = new Job(key.getName());

            job.setDescription("Backup of " + backupDefinition.id());
            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());

            jobService.add(job);
        }
    }

    @Override public void jobExecutionVetoed(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getGroup().equals("backups")) {

            System.out.println("jobExecutionVetoed");
        }
    }

    @Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

        JobKey key = context.getJobDetail().getKey();
        if (key.getGroup().equals("backups")) {

            JobDataMap map = context.getMergedJobDataMap();
            BackupDefinition backupDefinition = (BackupDefinition) map.get("backupDefinition");

            Job job = jobService.get(key.getName());

            if (jobException == null) {
                job.setStatus(JobStatus.COMPLETED);
            } else {
                job.setStatus(JobStatus.FAILED);
            }

            job.setEndTime(LocalDateTime.now());

            jobService.add(job);
        }
    }
}
