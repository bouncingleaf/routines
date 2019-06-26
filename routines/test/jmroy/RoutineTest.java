package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutineTest {
    private Routine testRoutine;
    private final String TITLE = "Routine Title";

    @BeforeEach
    void beforeEach() {
        testRoutine = new Routine(TITLE);
    }

    /**
     * Adds a timed task and an untimed task
     */
    private void addTwo() {
        final String TIMED = "Timed Task";
        final String UNTIMED = "Untimed Task";
        testRoutine.addTask(new TimedTask(TIMED, 3));
        testRoutine.addTask(new UntimedTask(UNTIMED));
    }

    @Test
    void getTitle() {
        assertEquals(TITLE, testRoutine.getTitle());
    }

    /**
     * Tests for numberOfTasks and addTask
     */
    @Test
    void numberOfTasks() {
        // Empty
        assertEquals(0, testRoutine.numberOfTasks());
        // Non-empty
        addTwo();
        assertEquals(2, testRoutine.numberOfTasks());
    }

}
