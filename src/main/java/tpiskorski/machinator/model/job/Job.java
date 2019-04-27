package tpiskorski.machinator.model.job;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.Objects;

public class Job {

    private String id;

    private StringProperty description = new SimpleStringProperty("");
    private ObjectProperty<JobStatus> status = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDateTime> startTime = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> endTime = new SimpleObjectProperty<>();

    private JobType jobType;
    private String errorCause;

    public Job(String id) {
        this.id = id;
        setStatus(JobStatus.INITIALIZED);
    }

    static Callback<Job, Observable[]> extractor() {
        return (Job job) -> new Observable[]{
            job.descriptionProperty(), job.statusProperty(),
            job.startTimeProperty(), job.endTimeProperty(),
        };
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobStatus getStatus() {
        return status.get();
    }

    public void setStatus(JobStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<JobStatus> statusProperty() {
        return status;
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
