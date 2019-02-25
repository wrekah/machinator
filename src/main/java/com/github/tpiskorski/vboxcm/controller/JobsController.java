package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Job;
import com.github.tpiskorski.vboxcm.domain.JobRepository;
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

    @Autowired private JobRepository jobRepository;


    public void initialize() {
        jobs.setItems(jobRepository.getJobsList());
    }

}
