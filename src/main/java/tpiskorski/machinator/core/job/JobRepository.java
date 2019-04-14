package tpiskorski.machinator.core.job;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Optional;

@Repository
class JobRepository {

    private final ObservableList<Job> jobObservableList = FXCollections.observableArrayList(Job.extractor());

    void add(Job job) {
        jobObservableList.add(job);
    }

    ObservableList<Job> getJobsList() {
        return jobObservableList;
    }

    void remove(Job job) {
        jobObservableList.remove(job);
    }

    public Optional<Job> get(String id) {
        return jobObservableList.stream()
            .filter(job -> job.getId().equals(id))
            .findFirst();
    }

    public Job getLastById(String id){
        return jobObservableList.stream()
            .filter(job -> job.getId().equals(id))
            .filter(job -> job.getStatus() == JobStatus.IN_PROGRESS)
            .max(Comparator.comparing(Job::getStartTime))
            .get();
    }

    public Job getLastByType(JobType jobType) {
        return jobObservableList.stream()
            .filter(job -> job.getJobType() == jobType)
            .filter(job -> job.getStatus() == JobStatus.IN_PROGRESS)
            .max(Comparator.comparing(Job::getStartTime))
            .get();
    }
}
