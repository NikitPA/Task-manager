package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> allTypeTask = new ArrayList<>();

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

    public Task getTaskById(long id) {
        Task taskById;
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getId() == id) {
                taskById = allTypeTask.get(i);
                return taskById;
            }
        }
        return null;
    }

    public boolean updateTaskOrEpic(Task updateTask, long id) {
        Task task = getTaskById(id);
        if (task == null)
            return false;
        removeTaskOrEpic(id);
        allTypeTask.add(updateTask);
        return true;
    }

    public void removeTaskOrEpic(long idRemoveTask) {
        Task task = getTaskById(idRemoveTask);
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
        Task subtask = getTaskById(id);
        if (subtask == null)
            return false;
        removeSubtask(id, epicSubtask);
        allTypeTask.add(updateTask);
        return true;
    }

    public void removeSubtask(long idRemoveSubtask, Epic epicSubtask) {
        Task subtask = getTaskById(idRemoveSubtask);
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
            if (allTypeTask.get(i).getClass() == new SubTask("", "", 0, 0).getClass())
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (subTask.getStatus().equals("DONE")) {
            } else {
                return false;
            }
        }
        epic.setStatus("DONE");
        return true;
    }

    public boolean setStatusNewEpic(Epic epic) {
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getClass() == new SubTask("", "", 0, 0).getClass())
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (subTask.getStatus().equals("NEW")) {
            } else {
                return false;
            }
        }
        epic.setStatus("NEW");
        return true;
    }

    public boolean setStatusProgressEpic(Epic epic) {
        if (!setStatusDoneEpic(epic) && !setStatusNewEpic(epic)) {
            epic.setStatus("IN_PROGRESS");
            return true;
        }
        return false;
    }
}
