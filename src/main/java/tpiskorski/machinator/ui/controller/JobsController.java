package tpiskorski.machinator.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.core.job.Job;
import tpiskorski.machinator.core.job.JobService;
import tpiskorski.machinator.core.job.JobStatus;
import tpiskorski.machinator.ui.control.TooltipTableRow;

@Controller
public class JobsController {

    private final JobService jobService;

    @FXML private Button stopJobButton;
    @FXML private Button stopAllJobsButton;
    @FXML private TableView<Job> jobs;

    @Autowired public JobsController(JobService jobService) {
        this.jobService = jobService;
    }

    @FXML
    public void initialize() {
        jobs.setItems(jobService.getJobs());
        jobs.setRowFactory((tableView) -> new TooltipTableRow<>(Job::getDescription));

        BooleanBinding stopJobBinding = createStopJobBinding();
        BooleanBinding stopAllJobBinding = createStopAllJobsBinding();
        stopJobButton.disableProperty().bind(Bindings.isEmpty(jobs.getSelectionModel().getSelectedItems()).or(stopJobBinding));
        stopAllJobsButton.disableProperty().bind(Bindings.isEmpty(jobService.getJobs()).or(stopAllJobBinding));
    }

    private BooleanBinding createStopAllJobsBinding() {
        return Bindings.createBooleanBinding(() -> {
            boolean disableChangeType = false;

            if (jobService.allCompleted()) {
                disableChangeType = true;
            }
            return disableChangeType;
        }, jobs.getSelectionModel().selectedItemProperty());
    }

    private BooleanBinding createStopJobBinding() {
        return Bindings.createBooleanBinding(() -> {
            boolean disableChangeType = false;
            Job job = jobs.getSelectionModel().getSelectedItem();
            if (job == null || job.getStatus() != JobStatus.IN_PROGRESS) {
                disableChangeType = true;
            }
            return disableChangeType;
        }, jobs.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void stopAllJobs() {
        jobService.stopAllJobs();
    }

    @FXML
    public void stopJob() {
        Job selectedJob = jobs.getSelectionModel().getSelectedItem();
        jobService.stopJob(selectedJob);
    }
}
