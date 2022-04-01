package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTasksManager {

    public static final DateTimeFormatter FORMAT_DATE_CREATURE = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final Path path;

    private static FileBackedTasksManager newFileBacked = null;

    public FileBackedTasksManager(String path) {
        this.path = Paths.get(path);
    }

    public static FileBackedTasksManager loadFromFile(String path) {
        try {
            String unpacking = Files.readString(Paths.get(path));
            String[] str = unpacking.split("\n");
            newFileBacked = new FileBackedTasksManager(path);
            for (int i = 1; i < str.length; i++) {
                int count = 0;
                String[] str2 = str[i].split(",");
                if (str2[0].equals("")) {
                    count++;
                } else if (count == 1) {
                    for (String s : str2) {
                        newFileBacked.getTaskById(Integer.parseInt(s));
                    }
                } else {
                    TypeTasks type = TypeTasks.valueOf(str2[1]);
                    newFileBacked.createTasksByTypeTasks(type, str2);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return newFileBacked;
    }

    @Override
    public boolean addTaskOrEpic(Task task) {
        boolean isAdd = super.addTaskOrEpic(task);
        save();
        return isAdd;
    }

    @Override
    public boolean addSubTask(SubTask subTask) {
        boolean isAdd = super.addSubTask(subTask);
        save();
        return isAdd;
    }

    @Override
    public boolean removeTaskOrEpic(long idRemoveTask) {
        boolean isRemove = super.removeTaskOrEpic(idRemoveTask);
        save();
        return isRemove;
    }

    @Override
    public boolean removeAllSubtaskOfDeletedEpic(Epic epic) {
        boolean isRemove = super.removeAllSubtaskOfDeletedEpic(epic);
        save();
        return isRemove;
    }

    @Override
    public boolean removeAllTypeTask() {
        boolean isDelete = super.removeAllTypeTask();
        save();
        return isDelete;
    }

    @Override
    public boolean removeSubtask(long idRemoveSubtask) {
        boolean isRemoveSubtasks = super.removeSubtask(idRemoveSubtask);
        save();
        return isRemoveSubtasks;
    }

    @Override
    public boolean setStatusDoneEpic(Epic epic) {
        boolean isStatus = super.setStatusDoneEpic(epic);
        save();
        return isStatus;
    }

    @Override
    public boolean setStatusNewEpic(Epic epic) {
        boolean isStatus = super.setStatusNewEpic(epic);
        save();
        return isStatus;
    }

    @Override
    public boolean setStatusProgressEpic(Epic epic) {
        boolean isStatus = super.setStatusProgressEpic(epic);
        save();
        return isStatus;
    }

    @Override
    public boolean updateTaskOrEpic(Task updateTask, long id) {
        boolean isUpdate = super.updateTaskOrEpic(updateTask, id);
        save();
        return isUpdate;
    }

    @Override
    public boolean updateSubtask(SubTask updateTask, long id) {
        boolean isUpdate = super.updateSubtask(updateTask, id);
        save();
        return isUpdate;
    }

    @Override
    public void removeTaskInHistory(long id) {
        super.removeTaskInHistory(id);
        save();
    }

    @Override
    public boolean getTaskById(long id) {
        super.getTaskById(id);
        save();
        return true;
    }

    public void save() {
        try (Writer writer = new FileWriter(String.valueOf(path))) {
            if (Files.size(path) == 0) {
                writer.write("id,type,name,status,description,epic,duration,startTime,\n");
            }
            for (Task task : allTypeTask)
                writer.write(Objects.requireNonNull(toString(task)));
            writer.write("\n");
            for (int i = 0; i < historyManager.size(); i++)
                writer.write(historyManager.getHistory().get(i).getId() + ",");
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private String toString(Task task) {
        TypeTasks type = TypeTasks.valueOf(task.getClass().getSimpleName().toUpperCase());
        String infoTask = task.getId() + "," + type + "," + task.getTitle() + "," +
                task.getStatus() + "," + task.getDescription() + "," + task.getDuration() + "," +
                task.getStartTime().format(FORMAT_DATE_CREATURE) + ",";
        switch (type) {
            case SUBTASK:
                return infoTask + ((SubTask) task).getIdEpic() + "," + "\n";
            case TASK:
            case EPIC:
                return infoTask + "\n";
        }
        return null;
    }

    public void createTasksByTypeTasks(TypeTasks type, String[] str2) {
        switch (type) {
            case TASK:
                newFileBacked.addTaskOrEpic(new Task(str2[2], str2[4], Integer.parseInt(str2[0]),
                        Integer.parseInt(str2[5]), LocalDateTime.parse(str2[6], FORMAT_DATE_CREATURE)));
                break;
            case EPIC:
                newFileBacked.addTaskOrEpic(new Epic(str2[2], str2[4], Integer.parseInt(str2[0]),
                        Integer.parseInt(str2[5]), LocalDateTime.parse(str2[6], FORMAT_DATE_CREATURE)));
                break;
            case SUBTASK:
                newFileBacked.addSubTask(new SubTask(str2[2], str2[4], Integer.parseInt(str2[7]),
                        Integer.parseInt(str2[0]), Integer.parseInt(str2[5]),
                        LocalDateTime.parse(str2[6], FORMAT_DATE_CREATURE)));
                break;
        }
    }
}

