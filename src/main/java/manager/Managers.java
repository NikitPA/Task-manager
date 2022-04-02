package manager;

import server.HTTPTaskManager;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() {
        try {
            return new HTTPTaskManager("http://localhost:8078/register");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

    public static TaskManager getDefaultFileBacked() {
        return new FileBackedTasksManager("fileTasks.csv");
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
