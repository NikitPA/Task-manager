package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String title, String description, long id) {
        super(title, description, id);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
