package tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {

    private long idEpic;

    public SubTask(String title, String description, long idEpic, long id, int duration, LocalDateTime startTime) {
        super(title, description, id, duration, startTime);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SudTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
