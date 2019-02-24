package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Job;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

import java.time.Duration;

@Controller
public class JobsController {
    public TextField searchField;
    public Button stopJobButton;
    public Button stopAllJobsButton;
    public TableView<Job> jobs;

}
