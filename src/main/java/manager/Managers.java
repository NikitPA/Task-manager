package manager;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static TaskManager getDefaultFileBacked() {
        return new FileBackedTasksManager("fileTasks.csv");
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
