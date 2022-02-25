package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager newFileBacked = null;
        try {
            String unpacking = Files.readString(path);
            String[] str = unpacking.split("\n");
            newFileBacked = new FileBackedTasksManager(path);
            for (int i = 1; i < str.length; i++) {
                String[] str2 = str[i].split(",");
                if(str2.length > 2) {
                    //здесь непонятно как задать ограничение, просто двойка выглядит нелепо...
                    TypeTasks type = TypeTasks.valueOf(str2[1]);
                    switch (type) {
                        case TASK:
                            newFileBacked.addTaskOrEpic(new Task(str2[2], str2[4], Integer.parseInt(str2[0])));
                            break;
                        case EPIC:
                            newFileBacked.addTaskOrEpic(new Epic(str2[2], str2[4], Integer.parseInt(str2[0])));
                            break;
                        case SUBTASK:
                            newFileBacked.addSubTask(new SubTask(str2[2], str2[4], Integer.parseInt(str2[5]),
                                    Integer.parseInt(str2[0])), (Epic) newFileBacked.findTaskById(Integer.parseInt(str2[5])));
                            break;
                    }
                }
            }
            String[] str3 = str[str.length - 1].split(",");
            for (int j = 0; j < str3.length; j++) {
                newFileBacked.getTaskById(Integer.parseInt(str3[j]));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return newFileBacked;
    }

    @Override
    public void addTaskOrEpic(Task task) {
        super.addTaskOrEpic(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, Epic epicSubtasks) {
        super.addSubTask(subTask, epicSubtasks);
        save();
    }

    @Override
    public void removeTaskOrEpic(long idRemoveTask) {
        super.removeTaskOrEpic(idRemoveTask);
        save();
    }

    @Override
    public void removeAllSubtaskOfDeletedEpic(Epic epic) {
        super.removeAllSubtaskOfDeletedEpic(epic);
        save();
    }

    @Override
    public void removeAllTypeTask() {
        super.removeAllTypeTask();
        save();
    }

    @Override
    public void removeSubtask(long idRemoveSubtask, Epic epicSubtask) {
        super.removeSubtask(idRemoveSubtask, epicSubtask);
        save();
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
    public boolean updateSubtask(Task updateTask, long id, Epic epicSubtask) {
        boolean isUpdate = super.updateSubtask(updateTask, id, epicSubtask);
        save();
        return isUpdate;
    }

    @Override
    public void removeTaskInHistory(long id) {
        super.removeTaskInHistory(id);
        save();
    }

    @Override
    public void getTaskById(long id) {
        super.getTaskById(id);
        save();
    }

    private void save() {
        try (Writer writer = new FileWriter(String.valueOf(path));) {
            if (Files.size(path) == 0) {
                writer.write("id,type,name,status,description,epic,\n");
            }
            for (int i = 0; i < allTypeTask.size(); i++)
                writer.write(toString(allTypeTask.get(i)));
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
                task.getStatus() + "," + task.getDescription() + ",";
        switch (type) {
            case SUBTASK:
                return infoTask + ((SubTask) task).getIdEpic() + "," + "\n";
            case TASK:
            case EPIC:
                return infoTask + "\n";
        }
       return null;
    }
}

