package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    TaskManager taskManager;
    Task task;
    Task taskTwo;
    Task taskThree;
    Epic epic;
    Epic epicTwo;
    SubTask subTask;
    SubTask subTaskTwo;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("", "", 0, 20, LocalDateTime.of
                (2000, 1, 1, 1, 1));
        taskTwo = new Task("", "", 1, 20, LocalDateTime.of
                (2010, 1, 1, 1, 1));
        taskThree = new Task("", "", 10, 20, LocalDateTime.of
                (2015, 1, 1, 1, 1));
        epic = new Epic("", "", 2);
        epicTwo = new Epic("", "", 5);
        subTask = new SubTask("", "", 2, 3, 20, LocalDateTime.of
                (2020, 2, 2, 2, 2));
        subTaskTwo = new SubTask("", "", 2, 4, 20, LocalDateTime.of
                (2030, 2, 2, 2, 2));
    }

    @Test
    void addedTaskOrEpicShouldInList() {
        taskManager.addTaskOrEpic(task);
        assertEquals(List.of(task), taskManager.getAllTask());
        taskManager.addTaskOrEpic(taskThree);
        taskManager.addTaskOrEpic(taskTwo);
        assertIterableEquals((List.of(task, taskThree, taskTwo)), taskManager.getAllTask());
    }

    @Test
    void addedSubTaskNoByThatMethodNoShouldInList() {
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(subTask);
        assertIterableEquals((List.of(task, epic)), taskManager.getAllTypeTask());
    }

    @Test
    void addedSudTaskShouldInList() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        assertIterableEquals((List.of(subTask)), taskManager.getAllSubTaskOfEpic(epic.getId()));
        taskManager.addSubTask(subTaskTwo);
        assertIterableEquals(List.of(subTask, subTaskTwo), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void updatedEpicShouldUpdateInList() {
        taskManager.addTaskOrEpic(task);
        taskManager.updateTaskOrEpic(taskTwo, task.getId());
        assertEquals(List.of(taskTwo), taskManager.getAllTask());
        taskManager.updateTaskOrEpic(taskThree, taskTwo.getId());
        assertEquals(List.of(taskThree), taskManager.getAllTask());
    }

    @Test
    void epicWithSubtasksShouldUpdateEpicInListWithoutSubtasks() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(subTask);
        assertEquals(List.of(epic), taskManager.getAllEpic());
        taskManager.updateTaskOrEpic(epicTwo, epic.getId());
        assertEquals(List.of(epicTwo), taskManager.getAllEpic());
        assertEquals(Collections.emptyList(), taskManager.getAllSubTaskOfEpic(epicTwo.getId()));
    }

    @Test
    void updatedSubTaskNoByThatMethodNoShouldInList() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.updateTaskOrEpic(subTaskTwo, subTask.getId());
        assertEquals(List.of(subTask), taskManager.getAllSubTaskOfEpic(epic.getId()));
        assertNotEquals(List.of(subTaskTwo), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void updatedTypeTasksShouldMatchWithReplacedTypeTasks() {
        taskManager.addTaskOrEpic(task);
        taskManager.updateTaskOrEpic(epic, 0);
        assertEquals(List.of(task), taskManager.getAllTypeTask());
    }

    @Test
    void updatedSubtasksShouldInList() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        assertEquals(List.of(subTask), taskManager.getAllSubTaskOfEpic(epic.getId()));
        taskManager.updateSubtask(subTaskTwo, subTask.getId());
        assertEquals(List.of(subTaskTwo), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void InMethodUpdatedSubtasksShouldCorrectSecondParameters() {
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.updateSubtask(subTaskTwo, epic.getId());
        taskManager.updateSubtask(subTaskTwo, task.getId());
        assertEquals(List.of(subTask), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void remoteTaskOrEpicShouldDelete() {
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.removeTaskOrEpic(task.getId());
        taskManager.removeTaskOrEpic(epic.getId());
        assertEquals(Collections.emptyList(), taskManager.getAllTypeTask());
    }

    @Test
    void inMethodRemoteTaskOrEpicShouldGiveNoSubtask() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.removeTaskOrEpic(subTask.getId());
        assertEquals(List.of(subTask), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void inMethodRemoveSubtaskShouldPassCorrectParameter() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.removeSubtask(subTask.getId());
        assertEquals(Collections.emptyList(), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void inMethodRemoveSubtaskNoShouldPassNoCorrectParameters() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.removeSubtask(epic.getId());
        assertEquals(List.of(epic), taskManager.getAllEpic());
    }

    @Test
    void methodRemoveAllSubtaskOfDeletedEpicShouldDeleteAllSubtaskOfEpic() {
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTaskTwo);
        assertEquals(List.of(subTask, subTaskTwo), epic.getSubTasks());
    }

    @Test
    void removedAllTypeTaskShouldDelete() {
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(taskTwo);
        assertTrue(taskManager.removeAllTypeTask());
        assertEquals(Collections.emptyList(), taskManager.getAllTypeTask());
    }

    @Test
    void outputAllTypeTasksShouldGetAllTypeTasks() {
        addTasksInTaskManager();
        assertEquals(List.of(task, epic, epicTwo, subTask, subTaskTwo), taskManager.getAllTypeTask());
    }

    @Test
    void outputAllTypeTasksShouldGetAllTypeTasksWithoutTasksNoAdded() {
        addTasksInTaskManager();
        assertNotEquals(List.of(task, taskTwo, epic, epicTwo, subTask, subTaskTwo), taskManager.getAllTypeTask());
    }

    @Test
    void outputTasksShouldGetTasks() {
        addTasksInTaskManager();
        assertEquals(List.of(task), taskManager.getAllTask());
    }

    @Test
    void outputTasksShouldGetTasksWithoutTaskNoAdded() {
        addTasksInTaskManager();
        assertNotEquals(List.of(task, taskTwo), taskManager.getAllTask());
    }

    @Test
    void outputEpicShouldGetEpic() {
        addTasksInTaskManager();
        assertEquals(List.of(epic, epicTwo), taskManager.getAllEpic());
    }

    @Test
    void outputEpicsShouldGetTasksWithoutEpicNoAdded() {
        addTasksInTaskManager();
        assertNotEquals(List.of(epic), taskManager.getAllEpic());
    }

    @Test
    void outputSubtasksOfEpicShouldGetSubtasksOfEpic() {
        addTasksInTaskManager();
        assertEquals(List.of(subTask, subTaskTwo), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void outputSubtasksOfEpicShouldGetTasksWithoutSubtasksOfEpicNoAdded() {
        addTasksInTaskManager();
        assertNotEquals(List.of(subTask), taskManager.getAllSubTaskOfEpic(epic.getId()));
        assertNotEquals(List.of(), taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void outputPrioritizedTasksShouldGetTasksInRightOrder() {
        addTasksInTaskManager();
        assertIterableEquals(List.of(task, epic, subTask, subTaskTwo, epicTwo), taskManager.getPrioritizedTasks());
    }

    @Test
    void outputEmptyPrioritizedTasksShouldEmpty() {
        assertIterableEquals(Collections.emptyList(), taskManager.getPrioritizedTasks());
    }

    @Test
    void outputListHistoryShouldHaveRequestedTasks() {
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        assertEquals(List.of(task, epic), taskManager.getHistoryManager());
    }

    @Test
    void outputListHistoryNoShouldHaveRequestedTasks() {
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        assertNotEquals(List.of(task, epic, subTask), taskManager.getHistoryManager());
    }

    @Test
    void remoteTasksFromListHistoryNoShouldInListHistory() {
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.removeTaskInHistory(task.getId());
        assertEquals(List.of(epic), taskManager.getHistoryManager());
    }

    @Test
    void remoteTasksFromListHistoryShouldDelete() {
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.removeTaskInHistory(task.getId());
        assertNotEquals(List.of(epic, subTask), taskManager.getHistoryManager());
    }

    private void addTasksInTaskManager() {
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(epicTwo);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTaskTwo);
    }
}
