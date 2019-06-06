package tpiskorski.machinator.ui.controller.jobs;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.model.job.Job;
import tpiskorski.machinator.model.job.JobService;
import tpiskorski.machinator.ui.control.TooltipTableRow;

@Controller
public class JobsController {

    private final JobService jobService;

    @FXML private TableView<Job> jobs;

    @Autowired public JobsController(JobService jobService) {
        this.jobService = jobService;
    }

    @FXML
    public void initialize() {
        jobs.setItems(jobService.getJobs());
        jobs.setRowFactory((tableView) -> new TooltipTableRow());
    }

    @FXML
    public void clearJobs() {
        jobService.clear();
    }
}
