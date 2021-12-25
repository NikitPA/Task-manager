package Manager;

public class Task {
    private String title;
    private String description;
    private long id;
    private String status = "NEW";

    public Task(String title, String description, long id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    Task() {
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

    public String getStatus() {
        return status;
    }

    public void setStatusProgress() {
        this.status = "IN_PROGRESS";
    }

    public void setStatusDone() {
        this.status = "DONE";
    }

    public void setStatusNew() {
        this.status = "NEW";
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
}
