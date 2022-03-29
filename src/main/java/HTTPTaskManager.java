import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import manager.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    KVTaskClient client;

    public HTTPTaskManager(String path) throws IOException, InterruptedException {
        super(path);
        new KVTaskClient(path);
    }

    @Override
    public void save()  {
        List<Task> list = getAllTypeTask();
        Gson gson = new GsonBuilder().create();
        String text = gson.toJson(list);
        try {
            client.put(text, "1");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException, InterruptedException {
        Gson gson = new Gson();
        ArrayList<Task> task = gson.fromJson(client.load("task") , new TypeToken<ArrayList<Task>>(){}.getType());
        task.stream().map((this::addTaskOrEpic));
        ArrayList<Epic> epic = gson.fromJson(client.load("epic") , new TypeToken<ArrayList<Task>>(){}.getType());
        epic.stream().map((this::addTaskOrEpic));
        ArrayList<SubTask> subtask = gson.fromJson(client.load("subtask") , new TypeToken<ArrayList<Task>>(){}.getType());
        subtask.stream().map((this::addSubTask));

    }

}
