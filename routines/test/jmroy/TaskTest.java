package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private String TIMED_NAME = "TIMED task";
    private String UNTIMED_NAME = "UNTIMED task";
    private ArrayList<Task> tasks;

    @BeforeEach
    void beforeEach() {
        tasks = new ArrayList<>();
        // Implicit upcasting of TimedTask and UntimedTask to parent Task class
        tasks.add(new TimedTask(TIMED_NAME, -1, 4));
        tasks.add(new UntimedTask(UNTIMED_NAME, -1));
    }

    @Test
    void getName() {
        for (Task task : tasks) {
            assertEquals( task instanceof TimedTask ? TIMED_NAME : UNTIMED_NAME, task.getName());
        }
    }

    @Test
    void setName() {
        for (Task task : tasks) {
            String NEW_NAME = (task instanceof TimedTask ? TIMED_NAME : UNTIMED_NAME) + " new";
            task.setName(NEW_NAME);
            assertEquals( NEW_NAME, task.getName());
        }
    }

}