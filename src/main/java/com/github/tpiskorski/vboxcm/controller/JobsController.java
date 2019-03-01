package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Job;
import com.github.tpiskorski.vboxcm.domain.JobService;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class JobsController {
    public TextField searchField;
    public Button stopJobButton;
    public Button stopAllJobsButton;
    public TableView<Job> jobs;

    @Autowired private JobService jobService;

    public void initialize() {
        jobs.setItems(jobService.getJobs());
        stopJobButton.disableProperty().bind(Bindings.isEmpty(jobs.getSelectionModel().getSelectedItems()));
        stopAllJobsButton.disableProperty().bind(Bindings.isEmpty(jobService.getJobs()));
    }

    public void stopAllJobs() {
        jobService.stopAllJobs();
    }

    public void stopJob() {
        Job selectedJob = jobs.getSelectionModel().getSelectedItem();
        jobService.stopJob(selectedJob);
    }
}