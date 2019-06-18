package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UntimedTaskTest {
    private UntimedTask testTask;
    private String NAME = "Task name";

    @BeforeEach
    void beforeEach() {
        testTask = new UntimedTask(NAME, -1);
    }

    @Test
    void getTimeForDisplay() {
        assertEquals("(untimed)", testTask.getTimeForDisplay());
    }

    @Test
    void display() {
        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        testTask.display();
        assertEquals(
                NAME + "\t(untimed)\n",
                os.toString());

        //Restore normal output
        PrintStream originalOut = System.out;
        System.setOut(originalOut);
    }
}