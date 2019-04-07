package tpiskorski.machinator.core.job;

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
        job.setStatus("STOPPED");
        job.setProgress(100.0);
    }

    public void stopAllJobs() {
        for (Job job : jobRepository.getJobsList()) {
            stopJob(job);
        }
    }

    public ObservableList<Job> getJobs() {
        return jobRepository.getJobsList();
    }

    public void add(Job job) {
        jobRepository.add(job);
    }

    public void remove(Job job) {
        jobRepository.remove(job);
    }
}
