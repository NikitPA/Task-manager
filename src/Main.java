import manager.Managers;
import manager.TaskManager;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        LinkedList<Integer> list = new LinkedList<>();
        list.remove(0);
    }
}
