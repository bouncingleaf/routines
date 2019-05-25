package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final String USER = "test";
    private final String NAME = "Test Name";
    private User testUser;

    @BeforeEach
    void beforeEach() {
        testUser = new User(USER, NAME);
    }

    @Test
    // Tests deleteRoutines, addRoutine, getMyRoutines
    void testRoutines() {
        // Test user may have unknown number of routines from earlier testing
        // Clear it to start
        testUser.deleteRoutines();
        // Confirm that it is cleared
        ArrayList<Routine> testList = testUser.getMyRoutines();
        assert(testList.isEmpty());

        // Test adding a routine
        Routine testRoutine = new Routine("Test Routine");
        testUser.addRoutine(testRoutine);
        // Confirm that one was added
        assertEquals(1, testUser.getMyRoutines().size());

        // Now we KNOW test user has at least one routine
        // And we can do a valid test of deleteRoutines()
        testUser.deleteRoutines();
        // Confirm that it is cleared
        testList = testUser.getMyRoutines();
        assert(testList.isEmpty());
    }

    @Test
    void getData() {
        final String USER_TWO = USER + "2";
        final String USER_THREE = USER + "3";

        // Test user constructed with two arguments, second argument non-empty
        assertEquals(USER, testUser.getUserName());
        assertEquals(NAME, testUser.getName());

        // Test user constructed with second argument empty
        // userName should equal name
        User testTwo = new User(USER_TWO, "");
        assertEquals(USER_TWO, testTwo.getUserName());
        assertEquals(USER_TWO, testTwo.getName());

        // Test user constructed with no second argument
        // userName should equal name
        User testThree = new User(USER_THREE);
        assertEquals(USER_THREE, testThree.getUserName());
        assertEquals(USER_THREE, testThree.getName());
    }
}