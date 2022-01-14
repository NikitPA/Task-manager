package tasks;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private long id;
    private Status status = Status.NEW;

    public Task(String title, String description, long id) {
        this.title = title;
        this.description = description;
        this.id = id;
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
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
