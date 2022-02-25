import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Paths.get("fileTasks.csv"));
        fileBackedTasksManager.addTaskOrEpic(new Task("Make tasks", "make before 10 p.m.", 0));
        fileBackedTasksManager.addTaskOrEpic(new Task("Buy computer", "buy in monday", 1));
        fileBackedTasksManager.addTaskOrEpic(new Epic("Go to street", "Make its each day", 2));
        fileBackedTasksManager.addSubTask(new SubTask("Make it with sister", "today", 2, 3),
                new Epic("Go to street", "Make its each day", 2));
        fileBackedTasksManager.addSubTask(new SubTask("Go to street", "no later than 5 p.m.", 2, 4),
                new Epic("Go to street", "Make its each day", 2));
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getTaskById(2);

        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(Paths.get("fileTasks.csv"));
        System.out.println(fileBackedTasksManager1.getAllTypeTask());
    }

}
