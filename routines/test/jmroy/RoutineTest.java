package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class RoutineTest {
    private Routine testRoutine;
    private final String TITLE = "Routine Title";
    private final String TIMED = "Timed Task";
    private final String UNTIMED = "Untimed Task";

    @BeforeEach
    void beforeEach() {
        testRoutine = new Routine(TITLE);
    }

    /**
     * Adds a timed task and an untimed task
     */
    private void addTwo() {
        testRoutine.addTask(new TimedTask(TIMED, 3));
        testRoutine.addTask(new UntimedTask(UNTIMED));
    }

    @Test
    void getTitle() {
        assertEquals(TITLE, testRoutine.getTitle());
    }

    @Test
    void setTitle() {
        testRoutine.setTitle(TITLE + " NEW");
        assertEquals(TITLE + " NEW", testRoutine.getTitle());
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
        assertEquals( 2, testRoutine.numberOfTasks());
    }

    /**
     * Tests the display of the routine title and the tasks
     */
    @Test
    void display() {
        addTwo();
        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        testRoutine.display();
        assertEquals(
                "\tRoutine: " + TITLE + "\n" +
                        "\t\t1\t" + TIMED + "\t3 min\n" +
                        "\t\t2\t" + UNTIMED + "\t(untimed)\n",
                os.toString());

        //Restore normal output
        System.setOut(System.out);
    }

    @Test
    void edit() {
        addTwo();
        // 1. Change the routine title
        testEdit("1\n" + "New title!" + "\n\n");
        assertEquals("New title!", testRoutine.getTitle());
        // 2. Add tasks
        testEdit("2\nAdded timed task\n30\n\n\n");
        assertEquals(3, testRoutine.numberOfTasks());
        assertEquals("Added timed task 30 min", testRoutine.getTaskByNumber(3));
        testEdit("2\nAdded untimed task\n\n\n\n");
        assertEquals(4, testRoutine.numberOfTasks());
        assertEquals("Added untimed task (untimed)", testRoutine.getTaskByNumber(4));
        // 3. Edit a task:
        //   3a. Keep timed
        testEdit("3\n1\nEdited Timed Name\n123\n\n\n");
        assertEquals("Edited Timed Name 123 min", testRoutine.getTaskByNumber(1));
        //   3b. Keep untimed
        testEdit("3\n2\nEdited Untimed Name\n\n\n\n");
        assertEquals("Edited Untimed Name (untimed)", testRoutine.getTaskByNumber(2));
        //   3c. Make timed into untimed
        testEdit("3\n3\nChanged to Untimed\n0\n\n\n");
        assertEquals("Changed to Untimed (untimed)", testRoutine.getTaskByNumber(3));
        //   3c. Make untimed into timed
        testEdit("3\n4\nChanged to Timed\n321\n\n\n");
        assertEquals("Changed to Timed 321 min", testRoutine.getTaskByNumber(4));
        // Tests
        System.setIn(System.in);
    }

    private void testEdit (String testString) {
        System.setIn(new ByteArrayInputStream(testString.getBytes()));
        testRoutine.edit(new Scanner(System.in));

    }
}