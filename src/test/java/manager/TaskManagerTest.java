package manager;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TaskManagerTest <E extends TaskManager>{

    TaskManager taskManager;
    Task task;
    Task taskTwo;
    Epic epic;
    Epic epicTwo;
    SubTask subTask;
    SubTask subTaskTwo;

    @BeforeEach
    public void beforeEach(){
        taskManager = Managers.getDefault();
        task = new Task("","",0,20, LocalDateTime.of
                (2000,1,1,1,1));
        taskTwo = new Task("","",1,20, LocalDateTime.of
                (2010,1,1,1,1));
        epic = new Epic("","",2,20, LocalDateTime.MAX);
        epicTwo = new Epic("","",5,20, LocalDateTime.MAX);
        subTask = new SubTask("","",2,3,20,LocalDateTime.of
                (2020,2,2,2,2));
        subTaskTwo = new SubTask("","",2,4,20,LocalDateTime.of
                (2030,2,2,2,2));
    }

    @Test
    public void addedTaskOrEpicShouldInList(){
        Assertions.assertTrue(taskManager.addTaskOrEpic(task));
        Assertions.assertTrue(taskManager.addTaskOrEpic(epic));
    }

    @Test
    public void addedSubTaskNoByThatMethodNoShouldInList(){
        taskManager.addTaskOrEpic(epic);
        Assertions.assertFalse(taskManager.addTaskOrEpic(subTask));
    }

    @Test
    public void addedSudTaskShouldInList(){
        taskManager.addTaskOrEpic(epic);
        Assertions.assertTrue(taskManager.addSubTask(subTask));
    }

    @Test
    public void updatedEpicShouldUpdateInList(){
        taskManager.addTaskOrEpic(task);
        Assertions.assertTrue(taskManager.updateTaskOrEpic(taskTwo , 0));
    }

    @Test
    public void epicWithSubtasksShouldUpdateEpicInListWithoutSubtasks(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(subTask);
        Assertions.assertTrue(taskManager.updateTaskOrEpic(epicTwo,2));
        Assertions.assertEquals(List.of() , taskManager.getAllSubTaskOfEpic(epicTwo.getId()));
    }

    @Test
    public void updatedSubTaskNoByThatMethodNoShouldInList(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertFalse(taskManager.updateTaskOrEpic(subTaskTwo,subTask.getId()));
    }

    @Test
    public void updatedTypeTasksShouldMatchWithReplacedTypeTasks(){
        taskManager.addTaskOrEpic(task);
        Assertions.assertFalse(taskManager.updateTaskOrEpic(epic , 0));
    }

    @Test
    public void updatedSubtasksShouldInList(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertTrue(taskManager.updateSubtask(subTaskTwo,subTask.getId()));
    }

    @Test
    public void InMethodUpdatedSubtasksShouldCorrectSecondParameters(){
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertFalse(taskManager.updateSubtask(subTaskTwo,epic.getId()));
        Assertions.assertFalse(taskManager.updateSubtask(subTaskTwo,task.getId()));
    }

    @Test
    public void remoteTaskOrEpicShouldDelete(){
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        Assertions.assertTrue(taskManager.removeTaskOrEpic(task.getId()));
        Assertions.assertTrue(taskManager.removeTaskOrEpic(epic.getId()));
    }

    @Test
    public void inMethodRemoteTaskOrEpicShouldGiveNoSubtask(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertFalse(taskManager.removeTaskOrEpic(subTask.getId()));
    }

    @Test
    public void inMethodRemoveSubtaskShouldPassCorrectParameter(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertTrue(taskManager.removeSubtask(subTask.getId()));
    }

    @Test
    public void inMethodRemoveSubtaskNoShouldPassNoCorrectParameters(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        Assertions.assertFalse(taskManager.removeSubtask(epic.getId()));
    }

    @Test
    public void methodRemoveAllSubtaskOfDeletedEpicShouldDeleteAllSubtaskOfEpic(){
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTaskTwo);
        Assertions.assertEquals(List.of(subTask,subTaskTwo) , epic.getSubTasks());
        taskManager.removeAllSubtaskOfDeletedEpic(epic);
        Assertions.assertEquals(List.of() , epic.getSubTasks());
    }
    @Test
    public void removedAllTypeTaskShouldDelete(){
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(taskTwo);
        Assertions.assertTrue(taskManager.removeAllTypeTask());
        Assertions.assertEquals(List.of(),taskManager.getAllTypeTask());
    }

    @Test
    public void outputAllTypeTasksShouldGetAllTypeTasks(){
        addTasksInTaskManager();
        Assertions.assertEquals(List.of(task,epic,epicTwo,subTask,subTaskTwo),taskManager.getAllTypeTask());
    }

    @Test
    public void outputAllTypeTasksShouldGetAllTypeTasksWithoutTasksNoAdded(){
        addTasksInTaskManager();
        Assertions.assertNotEquals(List.of(task,taskTwo,epic,epicTwo,subTask,subTaskTwo),taskManager.getAllTypeTask());
    }

    @Test
    public void outputTasksShouldGetTasks(){
        addTasksInTaskManager();
        Assertions.assertEquals(List.of(task),taskManager.getAllTask());
    }

    @Test
    public void outputTasksShouldGetTasksWithoutTaskNoAdded(){
        addTasksInTaskManager();
        Assertions.assertNotEquals(List.of(task , taskTwo),taskManager.getAllTask());
    }

    @Test
    public void outputEpicShouldGetEpic(){
        addTasksInTaskManager();
        Assertions.assertEquals(List.of(epic,epicTwo),taskManager.getAllEpic());
    }

    @Test
    public void outputEpicsShouldGetTasksWithoutEpicNoAdded(){
        addTasksInTaskManager();
        Assertions.assertNotEquals(List.of(epic),taskManager.getAllEpic());
    }

    @Test
    public void outputSubtasksOfEpicShouldGetSubtasksOfEpic(){
        addTasksInTaskManager();
        Assertions.assertEquals(List.of(subTask,subTaskTwo),taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    public void outputSubtasksOfEpicShouldGetTasksWithoutSubtasksOfEpicNoAdded(){
        addTasksInTaskManager();
        Assertions.assertNotEquals(List.of(subTask),taskManager.getAllSubTaskOfEpic(epic.getId()));
        Assertions.assertNotEquals(List.of(),taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    public void outputPrioritizedTasksShouldGetTasksInRightOrder(){
        addTasksInTaskManager();
        Assertions.assertIterableEquals(List.of(task,epic,subTask,subTaskTwo,epicTwo),taskManager.getPrioritizedTasks());
    }
//Как проверить разный тип коллекции на неравенство, не знаю. Метода assertNotIterableEquals нет... Если ли способ?

    @Test
    public void outputEmptyPrioritizedTasksShouldEmpty(){
        Assertions.assertIterableEquals(List.of(),taskManager.getPrioritizedTasks());
    }

    @Test
    public void outputListHistoryShouldHaveRequestedTasks(){
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        Assertions.assertEquals(List.of(task,epic) , taskManager.getHistoryManager());
    }

    @Test
    public void outputListHistoryNoShouldHaveRequestedTasks(){
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        Assertions.assertNotEquals(List.of(task,epic , subTask) , taskManager.getHistoryManager());
    }

    @Test
    public void remoteTasksFromListHistoryNoShouldInListHistory(){
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.removeTaskInHistory(task.getId());
        Assertions.assertEquals(List.of(epic) , taskManager.getHistoryManager());
    }

    @Test
    public void remoteTasksFromListHistoryShouldDelete(){
        addTasksInTaskManager();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(epic.getId());
        taskManager.removeTaskInHistory(task.getId());
        Assertions.assertNotEquals(List.of(epic, subTask) , taskManager.getHistoryManager());
    }

    private void addTasksInTaskManager(){
        taskManager.addTaskOrEpic(task);
        taskManager.addTaskOrEpic(epic);
        taskManager.addTaskOrEpic(epicTwo);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTaskTwo);
    }
}
