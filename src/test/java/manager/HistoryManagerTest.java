package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class HistoryManagerTest {

    @Test
    public void historyManagerShouldEmpty() {
        TaskManager taskManager = new InMemoryTasksManager();
        Assertions.assertEquals(Collections.emptyList(), taskManager.getHistoryManager());
    }

    @Test
    public void historyManagerShouldDuplicateTasks() {
        TaskManager taskManager = new InMemoryTasksManager();
        Task task = new Task("", "", 1, 20, LocalDateTime.MIN);
        taskManager.addTaskOrEpic(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, taskManager.getHistoryManager().size());
    }

    @Test
    public void addedTaksInHistoryManagerShouldBeInList() {
        TaskManager taskManager = new InMemoryTasksManager();
        Task task = new Task("", "", 1, 20, LocalDateTime.MIN);
        Task task1 = new Task("", "", 2, 20, LocalDateTime.MAX);
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(task1);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getHistoryManager();
        Assertions.assertEquals(List.of(task, task1), taskManager.getHistoryManager());
    }
}
