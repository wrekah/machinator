package com.github.tpiskorski.vboxcm.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepository {

    private ObservableList<Job> jobObservableList = FXCollections.observableArrayList(Job.extractor());

    public void add(Job job) {
        jobObservableList.add(job);
    }

    public ObservableList<Job> getJobsList() {
        return jobObservableList;
    }

    public void remove(Job job) {
        jobObservableList.remove(job);
    }
}
