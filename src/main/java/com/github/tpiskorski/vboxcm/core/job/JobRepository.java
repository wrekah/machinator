package com.github.tpiskorski.vboxcm.core.job;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

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
}
