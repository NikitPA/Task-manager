package tasks;

import manager.InMemoryTasksManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

public class EpicTest {

    TaskManager taskManager;
    Epic epic;
    SubTask subTask;
    SubTask subTask1;

    @Test
    public void epicListShouldEmpty(){
        taskManager = new InMemoryTasksManager();
        Assertions.assertEquals(Collections.emptyList(), taskManager.getAllEpic());
    }

    @Test
    public void allEpicShouldStatusNew(){
        createEpicWithSubTask();
        Assertions.assertEquals(Status.NEW , epic.getStatus());
    }

    @Test
    public void allEpicShouldStatusDone(){
        createEpicWithSubTask();
        subTask.setStatus(Status.DONE);
        subTask1.setStatus(Status.DONE);
        taskManager.setStatusDoneEpic(epic);
        Assertions.assertEquals(Status.DONE , epic.getStatus());
    }

    @Test
    public void allEpicShouldStatusDoneOrNew(){
        createEpicWithSubTask();
        subTask1.setStatus(Status.DONE);
        taskManager.setStatusProgressEpic(epic);
        Assertions.assertEquals(Status.PROGRESS , epic.getStatus());
    }

    @Test
    public void allEpicShouldStatusProgress(){
        createEpicWithSubTask();
        subTask.setStatus(Status.PROGRESS);
        subTask1.setStatus(Status.PROGRESS);
        taskManager.setStatusProgressEpic(epic);
        Assertions.assertEquals(Status.PROGRESS , epic.getStatus());
    }

    private void createEpicWithSubTask(){
        taskManager = new InMemoryTasksManager();
        epic = new Epic("" ,"",0);
        subTask = new SubTask("","",0,1,20,LocalDateTime.MIN);
        subTask1 = new SubTask("","",0,2,20,LocalDateTime.MIN);
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);
    }
}
