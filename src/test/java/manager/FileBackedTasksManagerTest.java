package manager;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManagerTest {

    FileBackedTasksManager fileBacked;
    Task task;
    Epic epic;
    SubTask subTask;
    SubTask subTaskTwo;

    @Test
    public void createEmptyFileBackedManagersShouldWithEmptyList() {
        fileBacked = new FileBackedTasksManager(Paths.get("fileTasks.csv"));
        Assertions.assertEquals(List.of(), fileBacked.getAllTypeTask());
    }

    @Test
    public void createFileBackedManagersWithEmptyListHistoryShouldWithoutTasksInListHistory() {
        createFileBackedManagersWithTasks();
        Assertions.assertEquals(List.of(), fileBacked.getHistoryManager());
    }

    @Test
    public void FileBackedManagersWithListHistoryShouldWithTasksInListHistory() {
        createFileBackedManagersWithTasks();
        fileBacked.getTaskById(task.getId());
        Assertions.assertEquals(List.of(task), fileBacked.getHistoryManager());
    }

    @Test
    public void loadedFileBackedManagersShouldLoadSavedFile() {
        createFileBackedManagersWithTasks();
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("fileTasks.csv"));
        Assertions.assertEquals(List.of(task, epic, subTask, subTaskTwo), fileBackedTasksManager.getAllTypeTask());
    }

    private void createFileBackedManagersWithTasks() {
        fileBacked = new FileBackedTasksManager(Paths.get("fileTasks.csv"));
        task = new Task("", "", 0, 20, LocalDateTime.of
                (2000, 1, 1, 1, 1));
        epic = new Epic("", "", 2, 20, LocalDateTime.MAX);
        subTask = new SubTask("", "", 2, 3, 20, LocalDateTime.of
                (2020, 2, 2, 2, 2));
        subTaskTwo = new SubTask("", "", 2, 4, 20, LocalDateTime.of
                (2030, 2, 2, 2, 2));
        fileBacked.addTaskOrEpic(task);
        fileBacked.addTaskOrEpic(epic);
        fileBacked.addSubTask(subTask);
        fileBacked.addSubTask(subTaskTwo);
    }
}
