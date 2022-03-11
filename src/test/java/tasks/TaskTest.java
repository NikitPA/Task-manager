package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    Task task;
    Task taskTwo;

    @BeforeEach
    void beforeEach() {
        task = new Task("", "", 0, 20, LocalDateTime.of
                (2000, 1, 1, 1, 1));
        taskTwo = new Task("", "", 0, 20, LocalDateTime.of
                (2000, 1, 1, 1, 1));

    }

    @Test
    void updatedFieldStatusTaskShouldBeUpdate() {
        assertEquals(Status.NEW, task.getStatus());
        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus());
        task.setStatus(Status.PROGRESS);
        assertEquals(Status.PROGRESS, task.getStatus());
    }

    @Test
    void equalsTwoTasksShouldTrue() {
        assertEquals(task, taskTwo);
    }

    @Test
    void outputTasksShouldCorrectly() {
        assertEquals("Task{title='', description='', id=0, status=NEW, " +
                "duration=20, startTime=2000-01-01T01:01}", task.toString());
    }
}
