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
        testTask = new UntimedTask(NAME);
    }

    @Test
    void getTimeForDisplay() {
        assertEquals("(untimed)", testTask.getTimeForDisplay());
    }

    @Test
    void getName() {
        assertEquals(NAME, testTask.getName());
    }

    @Test
    void setName() {
        String NEW_NAME = NAME.concat(" new");
        testTask.setName(NEW_NAME);
        assertEquals(NEW_NAME, testTask.getName());
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