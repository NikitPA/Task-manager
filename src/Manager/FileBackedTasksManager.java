package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

//Здравствуйте.
//Немного ввело в спутанье ТЗ. Да и решение моё сложно назвать решение, всё скомканно, очень не нравится.
//Скидываю ,чтобы проверили логику, после буду пытаться разбить на больше методов(возможно классов), для лучшего
//восприятия кода, сейчас ужас.
//Не понял для чего в ТЗ в подсказказ нам сказано завести енум, чем он тут нам поможет(если только там завести
//TASK("Task"), тогда возможно будет лучше сравнивать, но как понимаю, он еще для чего то, не подскажите?)
//Также непонятно про статические методы (в ТЗ в подсказке) (Напишите статические методы static String toString
// (HistoryManager manager) и static List<Integer> fromString(String value) для сохранения и восстановления менеджера
// истории из CSV.)      Для чего они статические? Никак не пойму.
//Если будет удобнее, можно и в слак ответить. Заранее спасибо большое!

public class FileBackedTasksManager extends InMemoryTasksManager implements TaskManager {
    private Path path;

    public FileBackedTasksManager(Path path) {
        super();
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
                if (str2.length == 5) {
                    if ("Task".equals(str2[1]))
                        newFileBacked.addTaskOrEpic(new Task(str2[2], str2[4], Integer.parseInt(str2[0])));
                    else
                        newFileBacked.addTaskOrEpic(new Epic(str2[2], str2[4], Integer.parseInt(str2[0])));
                }
                if (str2.length == 6)
                    newFileBacked.addSubTask(new SubTask(str2[2], str2[4], Integer.parseInt(str2[5]),
                            Integer.parseInt(str2[0])), (Epic) newFileBacked.findTaskById(Integer.parseInt(str2[5])));
            }
            String[] str3 = str[str.length - 1].split(",");
            for (int j = 0; j < str3.length; j++) {
                newFileBacked.getTaskById(Integer.parseInt(str3[j]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFileBacked;
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
            e.printStackTrace();
        }
    }

    private String toString(Task task) {
        String infoTask = task.getId() + "," + task.getClass().getSimpleName() + "," + task.getTitle() + "," +
                task.getStatus() + "," + task.getDescription() + ",";
        if ("SubTask".equals(task.getClass().getSimpleName())) {
            return infoTask + ((SubTask) task).getIdEpic() + "," + "\n";
        } else {
            return infoTask + "\n";
        }
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
}
