package tpiskorski.machinator.model.job;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void stopJob(Job job) {
        job.setStatus(JobStatus.STOPPED);
    }

    public void stopAllJobs() {
        for (Job job : jobRepository.getJobsList()) {
            stopJob(job);
        }
    }

    public ObservableList<Job> getJobs() {
        return jobRepository.getJobsList();
    }

    public boolean allCompleted() {
        return jobRepository.getJobsList().stream()
            .noneMatch(job -> job.getStatus() == JobStatus.IN_PROGRESS);
    }

    public void add(Job job) {
        jobRepository.add(job);
    }

    public void remove(Job job) {
        jobRepository.remove(job);
    }

    public Job get(String id) {
        Optional<Job> job = jobRepository.get(id);
        return job.get();
    }

    public Job getLastServerRefreshJob() {
        return jobRepository.getLastByType(JobType.SERVER_REFRESH);
    }

    public Job getLast(String id) {
        return jobRepository.getLastById(id);
    }
}
