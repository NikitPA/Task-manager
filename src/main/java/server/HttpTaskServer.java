package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
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
    private static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
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
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) -> new JsonPrimitive
                                (src.format(formatter))).create();
        server.createContext("/tasks/task/", exchange -> {
            try {
                processDifferentHttpMethodsTasks(exchange);
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/epic/", exchange -> {
            try {
                processDifferentHttpMethodsEpic(exchange);
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask/", exchange -> {
            try {
                processDifferentHttpMethodsSubtask(exchange);
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/subtask/epic/", exchange -> {
            try {
                processRequestForGetSubtasksOfEpic(exchange);
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/", exchange -> {
            try {
                processRequestOnOutputOrRemoveAllTypeTasks(exchange);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/tasks/", exchange -> {
            try {
                processRequestOnSearchTaskById(exchange);
            } catch (NullPointerException | NoSuchElementException e) {
                exchange.sendResponseHeaders(400, 0);
            } finally {
                exchange.close();
            }
        });
        server.createContext("/tasks/history", exchange -> {
            try {
                processRequestOnOutputHistory(exchange);
            } finally {
                exchange.close();
            }
        });
    }

    private void processDifferentHttpMethodsTasks(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "POST":
                String resultPost = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultPost.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                }
                JsonElement jsonElementPost = JsonParser.parseString(resultPost);
                JsonObject objectPost = jsonElementPost.getAsJsonObject();
                String title = objectPost.get(TITLE).getAsString();
                String description = objectPost.get(DESCRIPTION).getAsString();
                int id = objectPost.get(ID).getAsInt();
                int startTime = objectPost.get(DURATION).getAsInt();
                LocalDateTime localDateTime = LocalDateTime
                        .parse(objectPost.get(START_TIME).getAsString(), formatter);
                Objects.requireNonNull(taskManager)
                        .addTaskOrEpic(new Task(title, description, id, startTime, localDateTime));

                exchange.sendResponseHeaders(200, 0);
                break;
            case "PUT":
                String resultHttpPut = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultHttpPut.isEmpty()) {
                    exchange.sendResponseHeaders(403, 0);
                }
                JsonElement jsonElementHttpPut = JsonParser.parseString(resultHttpPut);
                JsonObject objectHttpPut = jsonElementHttpPut.getAsJsonObject();
                int idSearch = objectHttpPut.get(ID).getAsInt();
                Task updateTask = Objects.requireNonNull(taskManager).findTaskById(idSearch).get();
                updateTask.setTitle(objectHttpPut.get(TITLE).getAsString());
                updateTask.setDescription(objectHttpPut.get(DESCRIPTION).getAsString());
                updateTask.setStatus(Status.valueOf(objectHttpPut.get(STATUS).getAsString().toUpperCase()));
                updateTask.setDuration(objectHttpPut.get(DURATION).getAsInt());
                updateTask.setStartTime(LocalDateTime.parse(objectHttpPut.get(START_TIME).getAsString(), formatter));

                exchange.sendResponseHeaders(200, 0);
                break;
            case "DELETE":
                String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                if (rawQuery.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                int idRemoveTask = Integer.parseInt(rawQuery);
                taskManager.removeTaskOrEpic(idRemoveTask);
                exchange.sendResponseHeaders(200, 0);
                break;
            case "GET":
                List<Task> listTasks = Objects.requireNonNull(taskManager).getAllTask();

                String textResponse = gson.toJson(listTasks);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    private void processDifferentHttpMethodsEpic(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "POST":
                String resultHttpPost = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultHttpPost.isEmpty()) {
                    exchange.sendResponseHeaders(403, 0);
                }
                JsonElement jsonElementHttpPost = JsonParser.parseString(resultHttpPost);
                JsonObject objectHttpPost = jsonElementHttpPost.getAsJsonObject();
                String title = objectHttpPost.get(TITLE).getAsString();
                String descriptrion = objectHttpPost.get(DESCRIPTION).getAsString();
                int id = objectHttpPost.get(ID).getAsInt();
                Objects.requireNonNull(taskManager).addTaskOrEpic(new Epic(title, descriptrion, id));

                exchange.sendResponseHeaders(200, 0);
                break;
            case "PUT":
                String resultHttpPut = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultHttpPut.isEmpty()) {
                    exchange.sendResponseHeaders(403, 0);
                }
                JsonElement jsonElementHttpPut = JsonParser.parseString(resultHttpPut);
                JsonObject objectHttpPut = jsonElementHttpPut.getAsJsonObject();
                int idSearch = objectHttpPut.get(ID).getAsInt();
                Epic updateTask = (Epic) Objects.requireNonNull(taskManager).findTaskById(idSearch).get();
                updateTask.setTitle(objectHttpPut.get(TITLE).getAsString());
                updateTask.setDescription(objectHttpPut.get(DESCRIPTION).getAsString());

                exchange.sendResponseHeaders(200, 0);
                break;
            case "DELETE":
                String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                if (rawQuery.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                int idRemoveTask = Integer.parseInt(rawQuery);
                Objects.requireNonNull(taskManager).removeTaskOrEpic(idRemoveTask);

                exchange.sendResponseHeaders(200, 0);
                break;
            case "GET":
                List<Task> listEpic = Objects.requireNonNull(taskManager).getAllEpic();

                String textResponse = gson.toJson(listEpic);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    private void processDifferentHttpMethodsSubtask(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "POST":
                String resultHttpPost = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultHttpPost.isEmpty()) {
                    exchange.sendResponseHeaders(403, 0);
                }
                JsonElement jsonElementHttpPost = JsonParser.parseString(resultHttpPost);
                JsonObject objectHttpPost = jsonElementHttpPost.getAsJsonObject();
                String title = objectHttpPost.get(TITLE).getAsString();
                String description = objectHttpPost.get(DESCRIPTION).getAsString();
                int id = objectHttpPost.get(ID).getAsInt();
                int idEpic = objectHttpPost.get(ID_EPIC).getAsInt();
                int duration = objectHttpPost.get(DURATION).getAsInt();
                LocalDateTime localDateTime = LocalDateTime
                        .parse(objectHttpPost.get(START_TIME).getAsString(), formatter);
                Objects.requireNonNull(taskManager).addSubTask
                        (new SubTask(title, description, idEpic, id, duration, localDateTime));

                exchange.sendResponseHeaders(200, 0);
                break;
            case "PUT":
                String resultHttpPut = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (resultHttpPut.isEmpty()) {
                    exchange.sendResponseHeaders(403, 0);
                }
                JsonElement jsonElementHttpPut = JsonParser.parseString(resultHttpPut);
                JsonObject objectHttpPut = jsonElementHttpPut.getAsJsonObject();
                int idSearch = objectHttpPut.get(ID).getAsInt();
                SubTask task = (SubTask) Objects.requireNonNull(taskManager).findTaskById(idSearch).get();
                task.setTitle(objectHttpPut.get(TITLE).getAsString());
                task.setDescription(objectHttpPut.get(DESCRIPTION).getAsString());
                task.setStatus(Status.valueOf(objectHttpPut.get(STATUS).getAsString().toUpperCase()));
                task.setDuration(objectHttpPut.get(DURATION).getAsInt());
                task.setStartTime(LocalDateTime.parse(objectHttpPut.get(START_TIME).getAsString(), formatter));

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
                int idRemoveSubtask = Integer.parseInt(rawQuery);
                Objects.requireNonNull(taskManager).removeSubtask(idRemoveSubtask);

                exchange.sendResponseHeaders(200, 0);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    private void processRequestForGetSubtasksOfEpic(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                String rawQuery = exchange.getRequestURI().getRawQuery().split("=")[1];
                if (rawQuery.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                int idSearch = Integer.parseInt(rawQuery);
                List<SubTask> subtasksOfEpic = Objects.requireNonNull(taskManager).getAllSubTaskOfEpic(idSearch);
                if (subtasksOfEpic.isEmpty()) {
                    throw new NullPointerException();
                }
                String textResponse = gson.toJson(subtasksOfEpic);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    private void processRequestOnOutputOrRemoveAllTypeTasks(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                Set<Task> prioritizedTasks = Objects.requireNonNull(taskManager).getPrioritizedTasks();
                List<Task> list1 = prioritizedTasks.stream().filter(SubTask.class::isInstance)
                        .collect(Collectors.toList());
                list1.forEach(prioritizedTasks::remove);

                String textResponse = gson.toJson(prioritizedTasks);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
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
    }

    private void processRequestOnSearchTaskById(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                String rawQuery = exchange.getRequestURI().getRawQuery();
                if (rawQuery.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                String parameter = rawQuery.split("=")[1];
                if (parameter.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                int idSearch = Integer.parseInt(parameter);
                Task task = Objects.requireNonNull(taskManager).findTaskById(idSearch).get();

                String textResponse = gson.toJson(task);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    private void processRequestOnOutputHistory(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                List<Long> listHistory = taskManager.getHistoryManager().stream()
                        .map(Task::getId).collect(Collectors.toList());
                String textResponse = gson.toJson(listHistory);
                byte[] resp = textResponse.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
