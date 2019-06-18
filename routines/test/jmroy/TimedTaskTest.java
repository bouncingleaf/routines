package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TimedTaskTest {
    private TimedTask testTask;
    private String NAME = "Timed";
    private int MINUTES = 4;

    @BeforeEach
    void beforeEach() {
        testTask = new TimedTask(NAME, -1, MINUTES);
    }

    @Test
    void getMinutes() {
        assertEquals(MINUTES, testTask.getMinutes());
    }

    @Test
    void setMinutes() {
        testTask.setMinutes(MINUTES + 1);
        assertEquals(MINUTES + 1, testTask.getMinutes());
    }

    @Test
    void getTimeForDisplay() {
        assertEquals(MINUTES + " min", testTask.getTimeForDisplay());
    }

    @Test
    void display() {
        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        testTask.display();
        assertEquals(
                NAME + "\t" + MINUTES + " min\n",
                os.toString());

        //Restore normal output
        PrintStream originalOut = System.out;
        System.setOut(originalOut);
    }
}