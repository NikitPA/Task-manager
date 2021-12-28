package tasks;

public class SubTask extends Task {

    private long idEpic;

    public SubTask(String title, String description, long idEpic, long id) {
        super(title, description, id);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
