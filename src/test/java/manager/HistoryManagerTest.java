package manager;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoryManagerTest {

    @Test
    public void historyManagerShouldEmpty() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertEquals(new ArrayList<>(), taskManager.getHistoryManager());
    }

    @Test
    public void historyManagerShouldDuplicateTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("", "", 1, 20, LocalDateTime.MIN);
        taskManager.addTaskOrEpic(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, taskManager.getHistoryManager().size());
    }
}
