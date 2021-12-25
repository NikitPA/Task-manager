package Manager;

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
            if (allTypeTask.get(i).getClass() == new Task().getClass())
                allTask.add(allTypeTask.get(i));
        }
        return allTask;
    }

    public List<Task> getAllEpic() {
        List<Task> allEpic = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getClass() == new Epic().getClass()) {
                allEpic.add(allTypeTask.get(i));
            }
        }
        return allEpic;
    }

    public List<Task> getAllSubTaskOfEpic(long id) {
        List<Task> allSubTaskOfEpic = new ArrayList<>();
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getClass() == new SubTask().getClass())
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (id == subTask.getIdEpic())
                allSubTaskOfEpic.add(subTask);
        }
        return allSubTaskOfEpic;
    }

    public void addAnyTask(Task task) {
        allTypeTask.add(task);
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

    public boolean updateTask(Task updateTask, long id) {
        Task task = getTaskById(id);
        if (task == null)
            return false;
        removeTask(task);
        allTypeTask.add(updateTask);
        return true;
    }

    public void removeTask(Task deleteTask) {
        allTypeTask.remove(deleteTask);
    }

    public void removeAllTypeTask() {
        allTypeTask.clear();
    }

    public boolean setStatusDoneEpic(Epic epic) {
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getClass() == new SubTask().getClass())
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (subTask.getStatus() == "DONE") {
            } else {
                return false;
            }
        }
        epic.setStatusDone();
        return true;
    }

    public boolean setStatusNewEpic(Epic epic) {
        List<SubTask> allSubTask = new ArrayList<>();
        for (int i = 0; i < allTypeTask.size(); i++) {
            if (allTypeTask.get(i).getClass() == new SubTask().getClass())
                allSubTask.add((SubTask) allTypeTask.get(i));
        }
        for (SubTask subTask : allSubTask) {
            if (subTask.getStatus() == "NEW") {
            } else {
                return false;
            }
        }
        epic.setStatusNew();
        return true;
    }

    public boolean setStatusProgressEpic(Epic epic) {
        if (!setStatusDoneEpic(epic) && !setStatusNewEpic(epic)) {
            epic.setStatusProgress();
            return true;
        }
        return false;
    }
}
