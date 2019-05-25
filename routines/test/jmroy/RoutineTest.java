package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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
        PrintStream originalOut = System.out;
        System.setOut(originalOut);
    }
}