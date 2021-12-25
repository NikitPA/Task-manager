package Manager;

public class Epic extends Task {

    public Epic(String title, String description, long id) {
        super(title, description, id);
    }

    protected Epic() {
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
