package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTasksManager implements TaskManager {
    protected List<Task> allTypeTask = new ArrayList<>();
    protected InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistoryManager();
    protected Set<Task> prioritizedTasks;

    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    public void removeTaskInHistory(long id) {
        historyManager.remove(id);
    }

    public Set<Task> getPrioritizedTasks() {
        prioritizedTasks = new TreeSet<>();
        prioritizedTasks.addAll(allTypeTask);
        return prioritizedTasks;
    }

    public void getTaskById(long id) {
        Task taskById = findTaskById(id).get();
        if (historyManager.size() < 10) {
            historyManager.add(taskById);
        } else {
            historyManager.removeFirstNode();
            historyManager.add(taskById);
        }
    }

    protected Optional<Task> findTaskById(long id) {
        Optional<Task> task = allTypeTask.stream().filter(task1 -> task1.getId() == id).findFirst();
        if(task.isPresent())
            return task;
        return Optional.empty();
    }

    public List<Task> getAllTypeTask() {
        return allTypeTask;
    }

    public List<Task> getAllTask() {
        return allTypeTask.stream().
                filter(task -> !(Epic.class.isInstance(task))).
                filter(task -> !(SubTask.class.isInstance(task))).
                collect(Collectors.toList());
    }

    public List<Task> getAllEpic() {
        return allTypeTask.stream().filter(Epic.class::isInstance)
                .collect(Collectors.toList());
    }

    public List<SubTask> getAllSubTaskOfEpic(long idEpic) {
        if (!findTaskById(idEpic).isPresent() && !(Epic.class.isInstance(findTaskById(idEpic))))
            return Collections.emptyList();
        Epic epic = (Epic) findTaskById(idEpic).get();
        return epic.getSubTasks();
    }

    public boolean addTaskOrEpic(Task task) {
        if (!(SubTask.class.isInstance(task))) {
            allTypeTask.add(task);
            getPrioritizedTasks();
            if (intersectionTasks()) {
                allTypeTask.remove(task);
                getPrioritizedTasks();
            }
            return true;
        }
        return false;
    }

    public boolean addSubTask(SubTask subTask) {
        allTypeTask.add(subTask);
        Epic epic = (Epic) findTaskById(subTask.getIdEpic()).get();
        epic.setSubTasks(subTask);
        epic.setStartTime();
        epic.setDuration();
        getPrioritizedTasks();
        if (intersectionTasks()) {
            allTypeTask.remove(subTask);
            getPrioritizedTasks();
        }
        return true;
    }


    public boolean updateTaskOrEpic(Task updateTask, long id) {
        Task task = findTaskById(id).get();
        if (task == null || updateTask instanceof SubTask ||
                !(updateTask.getClass().getSimpleName().equals(task.getClass().getSimpleName())))
            return false;
        removeTaskOrEpic(id);
        allTypeTask.add(updateTask);
        getPrioritizedTasks();
        intersectionTasks();
        return true;
    }

    public boolean updateSubtask(SubTask updateSubtask, long idRemoveSubtask) {
        boolean isRemoveSubtasks = removeSubtask(idRemoveSubtask);
        if (!isRemoveSubtasks) {
            return false;
        }
        allTypeTask.add(updateSubtask);
        Epic epic = (Epic)findTaskById(updateSubtask.getIdEpic()).get();
        epic.setSubTasks(updateSubtask);
        getPrioritizedTasks();
        intersectionTasks();
        return true;
    }

    public boolean removeSubtask(long idRemoveSubtask) {
        if (!(findTaskById(idRemoveSubtask).get() instanceof SubTask))
            return false;
        SubTask subtask = (SubTask) findTaskById(idRemoveSubtask).get();
        if (subtask == null || !(findTaskById(subtask.getIdEpic()).get() instanceof Epic))
            return false;
        Epic epicSubtask = (Epic) findTaskById(subtask.getIdEpic()).get();
        allTypeTask.remove(subtask);
        epicSubtask.getSubTasks().remove(subtask);
        historyManager.remove(idRemoveSubtask);
        return true;
    }

    public boolean removeTaskOrEpic(long idRemoveTask) {
        Task task = findTaskById(idRemoveTask).get();
        if (task == null || task instanceof SubTask)
            return false;
        if (task instanceof Epic)
            removeAllSubtaskOfDeletedEpic((Epic) task);
        allTypeTask.remove(task);
        historyManager.remove(idRemoveTask);
        return true;
    }

    public boolean removeAllSubtaskOfDeletedEpic(Epic epic) {
        List<SubTask> subtaskOfEpic = epic.getSubTasks();
        allTypeTask.removeAll(subtaskOfEpic);
        for (int i = 0; i < subtaskOfEpic.size(); i++) {
            historyManager.remove(subtaskOfEpic.get(i).getId());
        }
        epic.getSubTasks().clear();
        return true;
    }

    public boolean removeAllTypeTask() {
        allTypeTask.clear();
        return true;
    }

    public boolean setStatusDoneEpic(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            if (subTask.getStatus().equals(Status.DONE)) {
            } else {
                return false;
            }
        }
        epic.setStatus(Status.DONE);
        return true;
    }

    public boolean setStatusNewEpic(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            if (subTask.getStatus().equals(Status.NEW)) {
            } else {
                return false;
            }
        }
        epic.setStatus(Status.NEW);
        return true;
    }

    public boolean setStatusProgressEpic(Epic epic) {
        if (!setStatusDoneEpic(epic) && !setStatusNewEpic(epic)) {
            epic.setStatus(Status.PROGRESS);
            return true;
        }
        return false;
    }

    private boolean intersectionTasks() {
        List<Task> list = new ArrayList<>();
        list.addAll(prioritizedTasks);
        for (int i = 0; i < list.size() - 1; i++) {
            if (!(list.get(i) instanceof Epic)) {
                if (list.get(i).getStartTime().plusMinutes(list.get(i).getDuration()).isAfter(list.get(i + 1).getStartTime()))
                    return true;
            }
        }
        return false;
    }
}
