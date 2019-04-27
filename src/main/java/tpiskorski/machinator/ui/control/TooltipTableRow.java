package tpiskorski.machinator.ui.control;

import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import tpiskorski.machinator.model.job.Job;

public class TooltipTableRow extends TableRow<Job> {

    @Override
    protected void updateItem(Job job, boolean empty) {
        super.updateItem(job, empty);
        if (job == null) {
            setTooltip(null);
        } else {
            String errorCause = job.getErrorCause();
            if (errorCause != null && !errorCause.isEmpty()) {
                Tooltip tooltip = new Tooltip(errorCause);
                setTooltip(tooltip);
            }
        }
    }
}