package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    List<Task> getAllTypeTask();

    List<Task> getAllTask();

    List<Task> getAllEpic();

    List<SubTask> getAllSubTaskOfEpic(long idEpic);

    Set<Task> getPrioritizedTasks();

    List<Task> getHistoryManager();

    void removeTaskInHistory(long id);

    Optional<Task> findTaskById(long id);

    boolean getTaskById(long id);

    boolean addTaskOrEpic(Task task);

    boolean addSubTask(SubTask subTask);

    boolean updateTaskOrEpic(Task updateTask, long id);

    boolean updateSubtask(SubTask updateTask, long id);

    boolean removeTaskOrEpic(long idRemoveTask);

    boolean removeSubtask(long idRemoveSubtask);

    boolean removeAllTypeTask();

    boolean setStatusDoneEpic(Epic epic);

    boolean setStatusNewEpic(Epic epic);

    boolean setStatusProgressEpic(Epic epic);
}
