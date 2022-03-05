package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTest {

    TaskManager taskManager;
    Epic epic;
    SubTask subTask;
    SubTask subTask1;

    @Test
    public void epicListShouldEmpty(){
        taskManager = Managers.getDefault();
        Assertions.assertEquals(new ArrayList<>(), taskManager.getAllEpic());
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
        taskManager = Managers.getDefault();
        epic = new Epic("" ,"",0,3, LocalDateTime.MIN);
        subTask = new SubTask("","",0,1,20,LocalDateTime.MIN);
        subTask1 = new SubTask("","",0,2,20,LocalDateTime.MIN);
        taskManager.addTaskOrEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);
    }
}
