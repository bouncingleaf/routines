package jmroy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    // Also tests addRoutine()
    void getMyRoutines() {
        User testUser = new User("My Name");
        ArrayList<Routine> testList = testUser.getMyRoutines();
        assert(testList.isEmpty());
        Routine testRoutine = new Routine("Test Routine");
        testUser.addRoutine(testRoutine);
        assertEquals(1, testUser.getMyRoutines().size());
    }

    @Test
    void getName() {
        User testUser = new User("My Name");
        assertEquals("My Name", testUser.getName());
    }
}