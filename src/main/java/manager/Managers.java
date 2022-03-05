package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
