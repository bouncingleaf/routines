package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class RoutineTest {
    private Routine testRoutine;

    @BeforeEach
    void beforeEach() {
        testRoutine = new Routine("Routine Title");
    }

    @Test
    void getTitle() {
        assertEquals("Routine Title", testRoutine.getTitle());
    }

    @Test
    void setTitle() {
        testRoutine.setTitle("New title");
        assertEquals("New title", testRoutine.getTitle());
    }

    @Test
    void numberOfTasksEmpty() {
        assertEquals(0, testRoutine.numberOfTasks());
    }

    @Test
    // Also tests addTask
    void numberOfTasksNonEmpty() {
        Task testTask = new TimedTask("hey", 3);
        Task testTaskTwo = new UntimedTask("foo");
        testRoutine.addTask(testTask);
        testRoutine.addTask(testTaskTwo);
        assertEquals( 2, testRoutine.numberOfTasks());
    }

    @Test
    void display() {
        Task testTask = new TimedTask("hey", 3);
        Task testTaskTwo = new UntimedTask("foo");
        testRoutine.addTask(testTask);
        testRoutine.addTask(testTaskTwo);

        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        testRoutine.display();
        assertEquals(
                "\tRoutine: Routine Title\n" +
                        "\t\t1\they\t3 min\n" +
                        "\t\t2\tfoo\t(untimed)\n",
                os.toString());

        //Restore normal output
        PrintStream originalOut = System.out;
        System.setOut(originalOut);
    }
}