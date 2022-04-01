package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpTaskServer {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final int PORT = 8080;
    private final HttpServer server;
    public final HTTPTaskManager taskManager = (HTTPTaskManager) Managers.getDefault();
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String ID = "id";
    static final String DURATION = "duration";
    static final String START_TIME = "startTime";
    static final String STATUS = "status";
    static final String ID_EPIC = "idEpic";

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) -> new JsonPrimitive
                                (src.format(formatter))).create();
        server.createContext("/tasks/task/", exchange -> {
            //ДОБАВЛЕНИЕ ЗАДАЧИ(1)++++++++++++
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        String result = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                        }
                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get(TITLE).getAsString();
                        // String s1 = object.get(TITLE).getAsString().isEmpty() ? object.get(TITLE).getAsString() :;
                        String s2 = object.get(DESCRIPTION).getAsString();
                        int s3 = object.get(ID).getAsInt();
                        int s4 = object.get(DURATION).getAsInt();
                        String s5 = object.get(START_TIME).getAsString();
                        LocalDateTime localDateTime = LocalDateTime.parse(s5, formatter);
                        Objects.requireNonNull(taskManager).addTaskOrEpic(new Task(s1, s2, s3, s4, localDateTime));

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    //ОБНОВЛЕНИЕ ЗАДАЧИ(2)+++++++++++++++++
                    case "PUT":
                        String result1 = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result1.isEmpty()) {
                            exchange.sendResponseHeaders(403, 0);
                        }
                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get(ID).getAsInt();
                        Task task = Objects.requireNonNull(taskManager).findTaskById(s33).get();
                        task.setTitle(object1.get(TITLE).getAsString());
                        task.setDescription(object1.get(DESCRIPTION).getAsString());
                        task.setStatus(Status.valueOf(object1.get(STATUS).getAsString().toUpperCase()));
                        task.setDuration(object1.get(DURATION).getAsInt());
                        task.setStartTime(LocalDateTime.parse(object1.get(START_TIME).getAsString(), formatter));

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    //+++++++++++++++++++++++++
                    case "DELETE":
                        String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                        if (rawQuery.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        int raw = Integer.parseInt(rawQuery);
                        taskManager.removeTaskOrEpic(raw);
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    case "GET":
                        List<Task> list = Objects.requireNonNull(taskManager).getAllTask();

                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/epic/", exchange -> {
            //ДОБАВЛЕНИЕ ЭПИКА(3)
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        String result = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result.isEmpty()) {
                            exchange.sendResponseHeaders(403, 0);
                        }
                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get(TITLE).getAsString();
                        String s2 = object.get(DESCRIPTION).getAsString();
                        int s3 = object.get(ID).getAsInt();
                        Objects.requireNonNull(taskManager).addTaskOrEpic(new Epic(s1, s2, s3));

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    //ОБНОВЛЕНИЕ ЭПИКА(4)
                    case "PUT":
                        String result1 = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result1.isEmpty()) {
                            exchange.sendResponseHeaders(403, 0);
                        }
                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get(ID).getAsInt();
                        Epic task = (Epic) Objects.requireNonNull(taskManager).findTaskById(s33).get();
                        task.setTitle(object1.get(TITLE).getAsString());
                        task.setDescription(object1.get(DESCRIPTION).getAsString());

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":
                        String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                        if (rawQuery.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        int raw = Integer.parseInt(rawQuery);
                        Objects.requireNonNull(taskManager).removeTaskOrEpic(raw);

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    case "GET":
                        List<Task> list = Objects.requireNonNull(taskManager).getAllEpic();

                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask/", exchange -> {
            //ДОБАВЕНИЕ ПОДЗАДАЧИ(5)
            try {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        String result = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result.isEmpty()) {
                            exchange.sendResponseHeaders(403, 0);
                        }
                        JsonElement jsonElement = JsonParser.parseString(result);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String s1 = object.get(TITLE).getAsString();
                        String s2 = object.get(DESCRIPTION).getAsString();
                        int s3 = object.get(ID).getAsInt();
                        int s6 = object.get(ID_EPIC).getAsInt();
                        int s4 = object.get(DURATION).getAsInt();
                        String s5 = object.get(START_TIME).getAsString();
                        LocalDateTime localDateTime = LocalDateTime.parse(s5, formatter);
                        Objects.requireNonNull(taskManager).addSubTask(new SubTask(s1, s2, s6, s3, s4, localDateTime));

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    //ОБНОВЛЕНИЕ ПОДЗАДАЧИ(6)
                    case "PUT":
                        String result1 = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        if (result1.isEmpty()) {
                            exchange.sendResponseHeaders(403, 0);
                        }
                        JsonElement jsonElement1 = JsonParser.parseString(result1);
                        JsonObject object1 = jsonElement1.getAsJsonObject();
                        int s33 = object1.get(ID).getAsInt();
                        SubTask task = (SubTask) Objects.requireNonNull(taskManager).findTaskById(s33).get();
                        task.setTitle(object1.get(TITLE).getAsString());
                        task.setDescription(object1.get(DESCRIPTION).getAsString());
                        task.setStatus(Status.valueOf(object1.get(STATUS).getAsString().toUpperCase()));
                        task.setDuration(object1.get(DURATION).getAsInt());
                        task.setStartTime(LocalDateTime.parse(object1.get(START_TIME).getAsString(), formatter));

                        Epic epic = (Epic) taskManager.findTaskById(task.getIdEpic()).get();
                        taskManager.setStatusProgressEpic(epic);
                        taskManager.setStatusNewEpic(epic);
                        taskManager.setStatusProgressEpic(epic);

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":
                        String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                        if (rawQuery.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        int raw = Integer.parseInt(rawQuery);
                        Objects.requireNonNull(taskManager).removeSubtask(raw);

                        exchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask/epic/", exchange -> {
            //ПОЛУЧЕНИЕ ПОДЗАДАЧ ЭПИКА(9)
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                        if (rawQuery.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        int raw = Integer.parseInt(rawQuery);
                        List<SubTask> list = Objects.requireNonNull(taskManager).getAllSubTaskOfEpic(raw);
                        if (list.isEmpty()) {
                            throw new NullPointerException();
                        }
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/", exchange -> {
            //ПОЛУЧЕНИЕ ВСЕХ ТИПОВ ЗАДАЧ(10)
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        Set<Task> list = Objects.requireNonNull(taskManager).getPrioritizedTasks();
                        List<Task> list1 = list.stream().filter(SubTask.class::isInstance)
                                .collect(Collectors.toList());
                        list1.forEach(list::remove);

                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    case "DELETE":
                        taskManager.removeAllTypeTask();
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/tasks/", exchange -> {
            //ПОЛУЧЕНИЕ ЗАДАЧИ ПО АЙДИ(11)
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        String s = exchange.getRequestURI().getRawQuery();
                        if (s.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        String rawQuery = s.split("=")[1];
                        if (rawQuery.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        int raw = Integer.parseInt(rawQuery);
                        Task task = Objects.requireNonNull(taskManager).findTaskById(raw).get();

                        String text = gson.toJson(task);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, resp.length);
                        exchange.getResponseBody().write(resp);
                        break;
                    default:
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/history", exchange -> {
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<Long> list = taskManager.getHistoryManager().stream()
                                .map(Task::getId).collect(Collectors.toList());
                        String text = gson.toJson(list);
                        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
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
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        taskManager.stop();
    }
}
