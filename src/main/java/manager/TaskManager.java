package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getAllTypeTask();//Checked

    List<Task> getAllTask();//Checked

    List<Task> getAllEpic();//Checked

    List<SubTask> getAllSubTaskOfEpic(long idEpic);//Checked

    Set<Task> getPrioritizedTasks();//Checked

    List<Task> getHistoryManager();//Checked

    void removeTaskInHistory(long id);//Checked

    void getTaskById(long id);//Checked

    boolean addTaskOrEpic(Task task);//Checked (два метода:один на добавление задачи и эпика , другой на добавление подзадачи
    //и не добавление ее в список)

    boolean addSubTask(SubTask subTask);//Checked(один метод на добавление сабтаски , другое передать мы не можем)

    boolean updateTaskOrEpic(Task updateTask, long id);//Checked(создано 4 метода проверки :
    //1.добавление задачи(нормальный исход)
    //2.добавление эпика(добавится новый эпик, без подзадач от старого)
    //3.добавление подзадачи (провекра на неправильный тип , как итог подзадача не измениться)
    //4. В аргументы переданы разные типы задач(мы не можем заменить эпик на задачу)

    boolean updateSubtask(SubTask updateTask, long id);//Checked(создано 3 метода проверки :
    //1.Нормальное поведение метода (изменение сабтаски)
    //2.Не нормальное поведение метода(неправильный переданный второй(Эпик) параметр)
    //3.Не нормальное поведение(неправильный переданный второй(Задача) параметр)

    boolean removeTaskOrEpic(long idRemoveTask);//Checked(создано 2 метода проверки :
    //1.Передам правильный параметр(задача или эпик)
    //2.Передан неправильный параметр(подзадача)

    boolean removeAllSubtaskOfDeletedEpic(Epic epic);//Checked(создано 1 метода проверки :
    //передан эпик с подзадачи ,сравниваем у эпика подзадачи(Ожидаемое и Получаемое), потом удаляем и список должен быть пустой

    boolean removeSubtask(long idRemoveSubtask);//Checked(создано 2 метода проверки :
    //1.Передам неправильный параметр(задача или эпик)
    //2.Передан правильный параметр(подзадача)

    boolean removeAllTypeTask();//Checked

    boolean setStatusDoneEpic(Epic epic);//Эти три метода проверены в EpicTest

    boolean setStatusNewEpic(Epic epic);

    boolean setStatusProgressEpic(Epic epic);
}
