package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTypeTask();

    List<Task> getAllTask();

    List<Task> getAllEpic();

    List<Task> getAllSubTaskOfEpic(long idEpic);

    void addTaskOrEpic(Task task);

    void addSubTask(SubTask subTask, Epic epicSubtasks);

    boolean updateTaskOrEpic(Task updateTask, long id);

    boolean updateSubtask(Task updateTask, long id, Epic epicSubtask);

    void removeTaskOrEpic(long idRemoveTask);

    void removeAllSubtaskOfDeletedEpic(Epic epic);

    void removeSubtask(long idRemoveSubtask, Epic epicSubtask);

    void removeAllTypeTask();

    boolean setStatusDoneEpic(Epic epic);

    boolean setStatusNewEpic(Epic epic);

    boolean setStatusProgressEpic(Epic epic);
}
