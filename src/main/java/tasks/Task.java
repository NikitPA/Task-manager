package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private String title;
    private String description;
    private long id;
    private Status status = Status.NEW;
    private int duration;
    private LocalDateTime startTime;

    public Task(String title, String description, long id, int duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, long id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Task o) {
        if (this.getStartTime().isBefore(o.getStartTime())) {
            return -1;
        } else if (!(this.getStartTime().isBefore(o.getStartTime()))) {
            return 1;
        } else
            return 0;
    }
}
