package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private transient int duration = 0;
    private transient LocalDateTime startTime = LocalDateTime.MAX;

    public Epic(String title, String description, long id, int duration, LocalDateTime startTime) {
        super(title,description,id,duration,startTime);
        this.duration = duration;
        this.startTime = startTime;
    }

    public Epic(String title, String description, long id) {
        super(title, description, id);
        setDuration();
        setStartTime();
    }

    protected ArrayList<SubTask> subTasks = new ArrayList<>();

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    public void setDuration() {
        for (int i = 0; i < subTasks.size(); i++) {
            duration += subTasks.get(i).getDuration();
        }
        super.setDuration(duration);
    }

    public void setStartTime() {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getStartTime().isBefore(startTime)) {
                startTime = subTasks.get(i).getStartTime();
            }
        }
        super.setStartTime(startTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
