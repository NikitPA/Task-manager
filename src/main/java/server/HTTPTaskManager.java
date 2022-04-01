package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    KVTaskClient client;
    Gson gson;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public HTTPTaskManager(String path) throws IOException, InterruptedException {
        super(path);
        client = new KVTaskClient(path);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) -> new JsonPrimitive
                                (src.format(formatter))).create();
    }

    @Override
    public void save() {
        List<Task> tasks = getAllTask();
        List<Task> epics = getAllEpic();
        List<SubTask> subtasks = new ArrayList<>();
        for (Task epic : epics) {
            subtasks.addAll(getAllSubTaskOfEpic(epic.getId()));
        }
        List<Task> history = getHistoryManager();
        String task = gson.toJson(tasks);
        String epic = gson.toJson(epics);
        String sub = gson.toJson(subtasks);
        String his = gson.toJson(history);
        try {
            client.put(task, "Task");
            client.put(epic, "Epic");
            client.put(sub, "SubTask");
            client.put(his, "History");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException, InterruptedException {
        gson = new Gson();
        ArrayList<Task> task = gson.fromJson(client.load("Task"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        task.stream().map(this::addTaskOrEpic);
        ArrayList<Epic> epic = gson.fromJson(client.load("Epic"),
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        epic.stream().map(this::addTaskOrEpic);
        ArrayList<SubTask> subtask = gson.fromJson(client.load("SubTask"),
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        subtask.stream().map(this::addSubTask);
        ArrayList<Task> history = gson.fromJson(client.load("History"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        history.stream().map(task1 -> this.getTaskById(task1.getId()));
    }

    public void stop() {
        client.stop();
    }
}
