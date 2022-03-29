import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HttpTaskServer {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final int PORT = 8080;
    final private HttpServer server;
    final private TaskManager taskManager = Managers.getDefaultFileBacked();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        InputStream inputStream = exchange.getRequestBody();
                        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                        String result = s.hasNext() ? s.next() : "";
                        if (result == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get("title").getAsString();
                        String s2 = object.get("description").getAsString();
                        int s3 = object.get("id").getAsInt();
                        int s4 = object.get("duration").getAsInt();
                        String s5 = object.get("startTime").getAsString();
                        LocalDateTime localDateTime = LocalDateTime.parse(s5, formatter);
                        taskManager.addTaskOrEpic(new Task(s1, s2, s3, s4, localDateTime));

                        String text = "Successful";
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    case "PUT":
                        InputStream is = exchange.getRequestBody();
                        Scanner sc = new Scanner(is).useDelimiter("\\A");
                        String result1 = sc.hasNext() ? sc.next() : "";
                        if (result1 == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get("id").getAsInt();
                        Task task = taskManager.getTaskById(s33);
                        if (task == null)
                            return;
                        task.setTitle(object1.get("title").getAsString());
                        task.setDescription(object1.get("description").getAsString());
                        task.setStatus(Status.valueOf(object1.get("status").getAsString().toUpperCase()));
                        task.setDuration(object1.get("duration").getAsInt());
                        task.setStartTime(LocalDateTime.parse(object1.get("startTime").getAsString(),formatter));

                        String text1 = "Successful";
                        byte[] resp1 = text1.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp1.length);
                        exchange.getResponseBody().write(resp1);
                        break;

                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/epic", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        InputStream inputStream = exchange.getRequestBody();
                        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                        String result = s.hasNext() ? s.next() : "";
                        if (result == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get("title").getAsString();
                        String s2 = object.get("description").getAsString();
                        int s3 = object.get("id").getAsInt();
                        taskManager.addTaskOrEpic(new Epic(s1, s2, s3));

                        String text = "Successful";
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    case "PUT":
                        InputStream is = exchange.getRequestBody();
                        Scanner sc = new Scanner(is).useDelimiter("\\A");
                        String result1 = sc.hasNext() ? sc.next() : "";
                        if (result1 == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get("id").getAsInt();
                        Epic task = (Epic) taskManager.getTaskById(s33);
                        if (task == null)
                            return;
                        task.setTitle(object1.get("title").getAsString());
                        task.setDescription(object1.get("description").getAsString());

                        String text1 = "Successful";
                        byte[] resp1 = text1.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp1.length);
                        exchange.getResponseBody().write(resp1);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        InputStream inputStream = exchange.getRequestBody();
                        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                        String result = s.hasNext() ? s.next() : "";
                        if (result == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get("title").getAsString();
                        String s2 = object.get("description").getAsString();
                        int s3 = object.get("id").getAsInt();
                        int s6 = object.get("idEpic").getAsInt();
                        int s4 = object.get("duration").getAsInt();
                        String s5 = object.get("startTime").getAsString();
                        LocalDateTime localDateTime = LocalDateTime.parse(s5, formatter);
                        taskManager.addSubTask(new SubTask(s1, s2, s6, s3, s4, localDateTime));

                        String text = "Successful";
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    case "PUT":
                        InputStream is = exchange.getRequestBody();
                        Scanner sc = new Scanner(is).useDelimiter("\\A");
                        String result1 = sc.hasNext() ? sc.next() : "";
                        if (result1 == null)
                            exchange.sendResponseHeaders(403, 0);

                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get("id").getAsInt();
                        SubTask task = (SubTask) taskManager.getTaskById(s33);
                        if (task == null)
                            return;
                        task.setTitle(object1.get("title").getAsString());
                        task.setDescription(object1.get("description").getAsString());
                        task.setStatus(Status.valueOf(object1.get("status").getAsString().toUpperCase()));
                        task.setDuration(object1.get("duration").getAsInt());
                        task.setStartTime(LocalDateTime.parse(object1.get("startTime").getAsString(),formatter));

                        Epic epic = (Epic) taskManager.getTaskById(task.getIdEpic());
                        taskManager.setStatusProgressEpic(epic);
                        taskManager.setStatusNewEpic(epic);
                        taskManager.setStatusProgressEpic(epic);

                        String text1 = "Successful";
                        byte[] resp1 = text1.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp1.length);
                        exchange.getResponseBody().write(resp1);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/task1", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Task> list = taskManager.getAllTask();
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter))).create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/epic1", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Task> list = taskManager.getAllEpic();
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter)))
                                .registerTypeAdapter(Epic.class, new TypeAdapter<Epic>() {
                                    @Override
                                    public void write(JsonWriter out, Epic value) throws IOException {
                                        out.beginObject();
                                        out.name("title");
                                        out.value(value.getTitle());
                                        out.name("description");
                                        out.value(value.getDescription());
                                        out.name("id");
                                        out.value(value.getId());
                                        out.name("status");
                                        out.value(value.getStatus().toString());
                                        out.name("duration");
                                        out.value(value.getDuration());
                                        out.name("startTime");
                                        out.value(value.getStartTime().format(formatter));
                                        out.endObject();
                                    }

                                    @Override
                                    public Epic read(JsonReader in) throws IOException {
                                        return null;
                                    }
                                }).create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask/epic/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        int raw = Integer.parseInt(exchange.getRequestURI().getRawQuery().split("=")[1]);
                        List<SubTask> list = taskManager.getAllSubTaskOfEpic(raw);
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter)))
                                .create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks", exchange -> {///////////////////////////
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Task> list = taskManager.getAllTypeTask();
                        List<Task> list1 = list.stream().filter(SubTask.class::isInstance)
                                .collect(Collectors.toList());
                        list.removeAll(list1);
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter)))
                                .create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/task/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        int raw = Integer.parseInt(exchange.getRequestURI().getRawQuery().split("=")[1]);
                        Task task = taskManager.getTaskById(raw);
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter)))
                                .registerTypeAdapter(Epic.class, new TypeAdapter<Epic>() {
                                    @Override
                                    public void write(JsonWriter out, Epic value) throws IOException {
                                        out.beginObject();
                                        out.name("title");
                                        out.value(value.getTitle());
                                        out.name("description");
                                        out.value(value.getDescription());
                                        out.name("id");
                                        out.value(value.getId());
                                        out.name("status");
                                        out.value(value.getStatus().toString());
                                        out.name("duration");
                                        out.value(value.getDuration());
                                        out.name("startTime");
                                        out.value(value.getStartTime().format(formatter));
                                        out.name("subtask");
                                        out.beginArray();
                                        for (SubTask subTask : value.getSubTasks()) {
                                            out.value(subTask.getTitle());
                                            out.value(subTask.getDuration());
                                            out.value(subTask.getStatus().toString());
                                            out.value(subTask.getDuration());
                                        }
                                        out.endArray();
                                        out.endObject();
                                    }
                                    @Override
                                    public Epic read(JsonReader in) throws IOException {
                                        return null;
                                    }
                                }).create();
                        String text = gson.toJson(task);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/task2/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "DELETE":
                        int raw = Integer.parseInt(exchange.getRequestURI().getRawQuery().split("=")[1]);
                        taskManager.removeTaskOrEpic(raw);
                        String text2 = "Successful";
                        byte[] resp2 = text2.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp2.length);
                        exchange.getResponseBody().write(resp2);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/epic2/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "DELETE":
                        int raw = Integer.parseInt(exchange.getRequestURI().getRawQuery().split("=")[1]);
                        taskManager.removeTaskOrEpic(raw);
                        String text2 = "Successful";
                        byte[] resp2 = text2.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp2.length);
                        exchange.getResponseBody().write(resp2);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask2/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "DELETE":
                        int raw = Integer.parseInt(exchange.getRequestURI().getRawQuery().split("=")[1]);
                        taskManager.removeSubtask(raw);
                        String text2 = "Successful";
                        byte[] resp2 = text2.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp2.length);
                        exchange.getResponseBody().write(resp2);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks2/", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "DELETE":
                        taskManager.removeAllTypeTask();
                        String text2 = "Successful";
                        byte[] resp2 = text2.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp2.length);
                        exchange.getResponseBody().write(resp2);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/history", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Task> list = taskManager.getHistoryManager();
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                                LocalDateTime.parse(json.getAsString()))
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                        (src, type, context) -> new JsonPrimitive
                                                (src.format(formatter))).create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/history5555", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Person> list = new ArrayList<>();
                        list.add(new Person(1 , "Bob" ,
                                List.of(new Son(1,"a") ,new Son(10,"ab"),new Son(111,"acccc"))));
                        list.add(new Person(10 , "Tob" ,
                                List.of(new Son(10,"ab"),new Son(111,"acccc"))));
                        Gson gson = new GsonBuilder().create();
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes("UTF-8");
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
            }
            } finally {
                exchange.close();
            }
        });
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }
}

class Person{
    private int id;
    private String name;
    private List<Son> list;

    public Person(int id, String name, List<Son> list) {
        this.id = id;
        this.name = name;
        this.list = list;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", list=" + list +
                '}';
    }
}
class Son{
    private int id;
    private String name;

    public Son(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

/*
class DwarfSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("Title", src.getTitle());
        object.addProperty("Description", src.getDescription());
        object.addProperty("Id", src.getId());
        object.addProperty("Duration", src.getDuration());
        object.addProperty("StartTime", src.getStartTime().
                format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        JsonArray array = new JsonArray();
        object.add("Subtask", array);
        for (SubTask subTask : src.getSubTasks()) {
            array.add(subTask.getTitle());
            array.add(subTask.getTitle());
            array.add(subTask.getDescription());
            array.add(subTask.getId());
            array.add(subTask.getIdEpic());
            array.add(subTask.getDuration());
        }
        return null;
    }
}*/
