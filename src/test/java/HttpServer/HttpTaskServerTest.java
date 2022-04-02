package HttpServer;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    HttpTaskServer server;
    KVServer kvServer;
    Task task;
    Epic epic;
    SubTask subTask;
    Gson gson;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, jsonDes) ->
                                LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) -> new JsonPrimitive
                                (src.format(formatter))).create();
        task = new Task("Z", "ZZZ", 0, 20, LocalDateTime.of
                (2000, 1, 1, 1, 1));
        epic = new Epic("T", "TTT", 2);
        subTask = new SubTask("E", "EEE", 2, 3, 20, LocalDateTime.of
                (2020, 2, 2, 2, 2));
    }

    @AfterEach
    void afterEach() {
        server.stop();
        kvServer.stop();
    }

    @Test
    void checkAddTaskByHttpShouldInHttpManager() throws IOException, InterruptedException {
        HttpResponse<String> response = addTaskByHttp();
        assertEquals(response.statusCode(), 200);
        assertEquals(List.of(task), server.taskManager.getAllTask());

    }

    @Test
    void checkAddTaskByHttpWithOutBodyShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllTask());
    }

    @Test
    void checkAddTaskByHttpWithNoCorrectShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"333333\",\n" +
                "\t\"id\": 17,\n" +
                "\t\"duration\": 20,\n" +
                "\t\"startTime\": \"15.07.2110 15:35\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllTask());
    }

    @Test
    void checkUpdateTaskByHttpShouldInHttpManager() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"Z\",\n" +
                "\t\"title\": \"ZZZ\",\n" +
                "\t\"id\": 0,\n" +
                "\t\"status\": NEW,\n" +
                "\t\"duration\": 30,\n" +
                "\t\"startTime\": \"01.01.2000 01:01\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        task.setDuration(30);
        assertEquals(List.of(task), server.taskManager.getAllTask());
    }

    @Test
    void checkUpdateTaskWithOutAnyFieldByHttpShouldMistake400() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"Z\",\n" +

                "\t\"id\": 0,\n" +
                "\t\"status\": NEW,\n" +
                "\t\"duration\": 30,\n" +
                "\t\"startTime\": \"01.01.2000 01:01\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(List.of(task), server.taskManager.getAllTask());
    }

    @Test
    void checkUpdateTaskWithOutBodyByHttpShouldMistake400() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 403);
        assertEquals(List.of(task), server.taskManager.getAllTask());
    }

    @Test
    void checkRemoveTaskWithExistentIdShouldDelete() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/?d=0");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllTask());
    }

    @Test
    void checkRemoveTaskByNoCorrectIdShouldDelete() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/?d=1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(List.of(task), server.taskManager.getAllTask());
    }

    @Test
    void checkGetAllTaskByHttpShould() throws IOException, InterruptedException {
        addTaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(server.taskManager.getAllTask(), List.of(task));
        assertNotEquals(Collections.EMPTY_LIST, server.taskManager.getAllTask());
    }

    @Test
    void checkAddEpicByHttpShouldInHttpManager() throws IOException, InterruptedException {
        HttpResponse<String> response = addEpicByHttp();
        assertEquals(response.statusCode(), 200);
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
    }

    @Test
    void checkAddEpicByHttpWithOutBodyShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 403);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllEpic());
    }

    @Test
    void checkAddEpicByHttpWithNoCorrectShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"BUY\",\n" +
                "\t\"id\": 7,\n" +
                "\t\"duration\": 20,\n" +
                "\t\"startTime\": \"15.07.2010 15:35\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllEpic());
    }

    @Test
    void checkUpdateEpicByHttpShouldInHttpManager() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"T\",\n" +
                "\t\"title\": \"TTTZZZZ\",\n" +
                "\t\"id\": 2\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        task.setTitle("TTTZZZZ");
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
    }

    @Test
    void checkUpdateEpicWithOutAnyFieldByHttpShouldMistake400() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"T\",\n" +
                "\t\"id\": 1\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
    }

    @Test
    void checkUpdateEpicWithOutBodyByHttpShouldMistake400() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 403);
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
    }

    @Test
    void checkRemoveEpicWithExistentIdShouldDelete() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/?d=2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllEpic());
    }

    @Test
    void checkRemoveEpicByNoCorrectIdShouldDelete() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/?d=1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
    }

    @Test
    void checkGetAllEpicByHttpShould() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(List.of(epic), server.taskManager.getAllEpic());
        assertNotEquals(Collections.EMPTY_LIST, server.taskManager.getAllEpic());
    }

    @Test
    void checkAddsubtaskByHttpShouldInHttpManager() throws IOException, InterruptedException {
        HttpResponse<String> response = addSubtaskByHttp();
        assertEquals(response.statusCode(), 200);
        assertTrue(server.taskManager.getAllSubTaskOfEpic(subTask.getIdEpic()).contains(subTask));
    }

    @Test
    void checkAddSubtaskByHttpWithOutBodyShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 403);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void checkAddSubtaskByHttpWithNoCorrectShouldMistake400() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"PCTBEjjjjjj7\",\n" +
                "\t\"id\": 13,\n" +
                "\t\"idEpic\": 1,\n" +
                "\t\"duration\": 20,\n" +
                "\t\"startTime\": \"18.03.2030 10:35\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllTask());
    }

    @Test
    void checkUpdateSubtaskByHttpShouldInHttpManager() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"E\",\n" +
                "\t\"title\": \"EEE\",\n" +
                "\t\"id\": 3,\n" +
                "\t\"status\": \"DONE\",\n" +
                "\t\"idEpic\": 2,\n" +
                "\t\"duration\": 30,\n" +
                "\t\"startTime\": \"02.02.2020 02:02\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        subTask.setDuration(30);
        subTask.setStatus(Status.DONE);
        assertTrue(server.taskManager.getAllSubTaskOfEpic(subTask.getIdEpic()).contains(subTask));
    }

    @Test
    void checkUpdateSubtaskWithOutAnyFieldByHttpShouldMistake400() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\n" +
                "\t\"description\": \"E\",\n" +
                "\t\"id\": 3,\n" +
                "\t\"status\": \"DONE\",\n" +
                "\t\"idEpic\": 2,\n" +
                "\t\"duration\": 20,\n" +
                "\t\"startTime\": \"02.02.2020 02:02\"\n" +
                "}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertTrue(server.taskManager.getAllSubTaskOfEpic(subTask.getIdEpic()).contains(subTask));
    }

    @Test
    void checkUpdateSubtaskWithOutBodyByHttpShouldMistake400() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = "";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 403);
        assertTrue(server.taskManager.getAllSubTaskOfEpic(subTask.getIdEpic()).contains(subTask));
    }

    @Test
    void checkRemoveSubtaskWithExistentIdShouldDelete() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?d=3");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(Collections.EMPTY_LIST, server.taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void checkRemoveSubtaskByNoCorrectIdShouldDelete() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?d=5");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
        assertTrue(server.taskManager.getAllSubTaskOfEpic(subTask.getIdEpic()).contains(subTask));
    }

    @Test
    void checkGetSubtaskOfEpicById() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?d=2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(List.of(subTask), server.taskManager.getAllSubTaskOfEpic(epic.getId()));
    }

    @Test
    void checkGetSubtaskOfEpicByNoCorrectId() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?d=5");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void checkGetSubtaskOfEpicWithoutParameter() throws IOException, InterruptedException {
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void checkGetAllTypeTasks() throws IOException, InterruptedException {
        addTaskByHttp();
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        epic.setSubTasks(subTask);
        assertIterableEquals(List.of(task, epic, subTask), server.taskManager.getPrioritizedTasks());
    }

    @Test
    void checkRemoveAllTypeTasks() throws IOException, InterruptedException {
        addTaskByHttp();
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertIterableEquals(Collections.emptyList(), server.taskManager.getPrioritizedTasks());
    }

    @Test
    void checkGetTasksById() throws IOException, InterruptedException {
        addTaskByHttp();
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/tasks/?d=0");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 200);
        assertEquals(task, server.taskManager.findTaskById(task.getId()).get());
    }

    @Test
    void checkGetTasksByNoCorrectId() throws IOException, InterruptedException {
        addTaskByHttp();
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/tasks/?d=111");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void checkGetTasksWithoutId() throws IOException, InterruptedException {
        addTaskByHttp();
        addSubtaskByHttp();
        URI url = URI.create("http://localhost:8080/tasks/tasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 400);
    }

    private HttpResponse<String> addSubtaskByHttp() throws IOException, InterruptedException {
        addEpicByHttp();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(subTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> addEpicByHttp() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> addTaskByHttp() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
