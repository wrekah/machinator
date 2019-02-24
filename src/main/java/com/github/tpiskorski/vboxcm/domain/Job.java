package com.github.tpiskorski.vboxcm.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Job {

    private StringProperty jobName = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty progress = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> startTime;

    public String getJobName() {
        return jobName.get();
    }

    public void setJobName(String jobName) {
        this.jobName.set(jobName);
    }

    public StringProperty jobNameProperty() {
        return jobName;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getProgress() {
        return progress.get();
    }

    public void setProgress(String progress) {
        this.progress.set(progress);
    }

    public StringProperty progressProperty() {
        return progress;
    }

    public LocalDateTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime.set(startTime);
    }

    public ObjectProperty<LocalDateTime> startTimeProperty() {
        return startTime;
    }
}
