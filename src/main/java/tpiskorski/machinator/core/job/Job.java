package tpiskorski.machinator.core.job;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.Objects;

public class Job {

    private String id;

    private StringProperty description = new SimpleStringProperty("");
    private StringProperty status = new SimpleStringProperty("");
    private DoubleProperty progress = new SimpleDoubleProperty();
    private ObjectProperty<LocalDateTime> startTime = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> endTime = new SimpleObjectProperty<>();

    public Job(String id) {
        this.id = id;
    }

    static Callback<Job, Observable[]> extractor() {
        return (Job job) -> new Observable[]{
            job.descriptionProperty(), job.statusProperty(),
            job.progressProperty(), job.startTimeProperty(),
            job.endTimeProperty()
        };
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getId() {
        return id;
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

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public DoubleProperty progressProperty() {
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

    public LocalDateTime getEndTime() {
        return endTime.get();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime.set(endTime);
    }

    public ObjectProperty<LocalDateTime> endTimeProperty() {
        return endTime;
    }

    public boolean isStopped() {
        return status.get().equals("STOPPED") && progress.get() == 100.0;
    }

    @Override public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Job)) {
            return false;
        }
        Job that = (Job) obj;

        return Objects.equals(this.getId(), that.getId());
    }
}
