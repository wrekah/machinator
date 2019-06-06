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

    public ObservableList<Job> getJobs() {
        return jobRepository.getJobsList();
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

    public void clear() {
        jobRepository.clear();
    }
}
