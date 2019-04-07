package tpiskorski.machinator.ui.control;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tpiskorski.machinator.core.job.Job;
import tpiskorski.machinator.core.job.JobStatus;

public class JobProgressBarTableCell implements Callback<TableColumn<Job, JobStatus>, TableCell<Job, JobStatus>> {

    @Override public TableCell<Job, JobStatus> call(TableColumn<Job, JobStatus> column) {
        return new TableCell<>() {

            private ProgressBar progressBar = new ProgressBar();

            {
                progressBar.setMaxWidth(Double.MAX_VALUE);
            }

            @Override protected void updateItem(JobStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item == JobStatus.IN_PROGRESS) {
                        progressBar.setStyle("-fx-accent: blue");
                        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    } else if (item == JobStatus.STOPPED || item == JobStatus.CANCELLED) {
                        progressBar.setStyle("-fx-accent: red");
                        progressBar.setProgress(100.0);
                    } else {
                        progressBar.setStyle("-fx-accent: green");
                        progressBar.setProgress(100.0);
                    }

                    setGraphic(progressBar);
                }
            }
        };
    }
}
