package manager;

import tasks.*;

import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    private List<Task> allTypeTask = new ArrayList<>();
    private InMemoryHistoryManager listHistory = new InMemoryHistoryManager();

    public List<Task> getListHistory() {
        return listHistory.getHistory();
    }

    public void removeTaskInHistory(long id) {
        listHistory.remove(id);
    }

    public void getTaskById(long id) {
        Task taskById = findTaskById(id);
        if (listHistory.size() < 10) {
            listHistory.add(taskById);
        } else {
            listHistory.remove(0);
            listHistory.add(taskById);
        }
    }

    private Task findTaskById(long id) {
        Task taskById;
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getId() == id) {
                taskById = allTypeTask.get(i);
                return taskById;
            }
        }
        return null;
    }

    public List<Task> getAllTypeTask() {
        return allTypeTask;
    }

    public List<Task> getAllTask() {
        List<Task> allTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (!(allTypeTask.get(i) instanceof Epic) && !(allTypeTask.get(i) instanceof SubTask))
                allTask.add(allTypeTask.get(i));
        }

        return allTask;
    }

    public List<Task> getAllEpic() {
        List<Task> allEpic = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i) instanceof Epic) {
                allEpic.add(allTypeTask.get(i));
            }
        }
        return allEpic;
    }

    public List<Task> getAllSubTaskOfEpic(long idEpic) {
        List<Task> allSubTaskOfEpic = new ArrayList<>();
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i) instanceof SubTask)
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (idEpic == subTask.getIdEpic())
                allSubTaskOfEpic.add(subTask);
        }
        return allSubTaskOfEpic;
    }

    public void addTaskOrEpic(Task task) {
        if (!(task instanceof SubTask)) {
            allTypeTask.add(task);
        } else {
            System.out.println("Для добавления SubTask другой метод");
        }
    }


    public void addSubTask(SubTask subTask, Epic epicSubtasks) {
        allTypeTask.add(subTask);
        epicSubtasks.setSubTasks(subTask);
    }


    public boolean updateTaskOrEpic(Task updateTask, long id) {
        Task task = findTaskById(id);
        if (task == null)
            return false;
        removeTaskOrEpic(id);
        allTypeTask.add(updateTask);
        return true;
    }

    public void removeTaskOrEpic(long idRemoveTask) {
        Task task = findTaskById(idRemoveTask);
        if (task == null)
            return;
        if (!(task instanceof SubTask)) {
            allTypeTask.remove(task);
        } else {
            System.out.println("Для удаления SubTask другой метод");
        }
        if (task instanceof Epic) {
            removeAllSubtaskOfDeletedEpic((Epic) task);
        } else {
            return;
        }
    }

    public void removeAllSubtaskOfDeletedEpic(Epic epic) {
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i) instanceof SubTask) {
                if (((SubTask) allTypeTask.get(i)).getIdEpic() == epic.getId()) {
                    allTypeTask.remove(allTypeTask.get(i));
                    i--;
                }
            }
        }
    }

    public boolean updateSubtask(Task updateTask, long id, Epic epicSubtask) {
        Task subtask = findTaskById(id);
        if (subtask == null)
            return false;
        removeSubtask(id, epicSubtask);
        allTypeTask.add(updateTask);
        return true;
    }

    public void removeSubtask(long idRemoveSubtask, Epic epicSubtask) {
        Task subtask = findTaskById(idRemoveSubtask);
        if (subtask == null)
            return;
        allTypeTask.remove(subtask);
        epicSubtask.getSubTasks().remove(subtask);
    }


    public void removeAllTypeTask() {
        allTypeTask.clear();
    }

    public boolean setStatusDoneEpic(Epic epic) {
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i) instanceof SubTask)
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (subTask.getStatus().equals(Status.DONE)) {
            } else {
                return false;
            }
        }
        epic.setStatus(Status.DONE);
        return true;
    }

    public boolean setStatusNewEpic(Epic epic) {
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i) instanceof SubTask)
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
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
}
