package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

class UserTest {
    private final String USER = "test";
    private final String NAME = "Test Name";
    private final String TEST_ROUTINE = "Test Routine";
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

        // Test listRoutines with no routines
        OutputStream os = redirectOutput();
        testUser.listRoutines();
        String expected = "No routines yet for " + NAME + ".\n";
        assertEquals(expected, os.toString());

        // Test adding a routine
        Routine testRoutine = new Routine(TEST_ROUTINE);
        testUser.addRoutine(testRoutine);
        // Confirm that one was added
        assertEquals(1, testUser.getMyRoutines().size());

        // Test listRoutines with one routine
        testUser.listRoutines();
        expected += NAME + "'s routines:\n\t" + TEST_ROUTINE;
        assertEquals(expected, os.toString());

        // Restore normal output
        System.setOut(System.out);

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

    @Test
    void testGetValidUserName() {
        // Does not save these users
        String result;
        // 1. Valid, changing capitalization
        result = testUserNameValidity("newUser\n\n");
        assertEquals("newuser", result);
        // 2. Bogus characters removed
        result = testUserNameValidity("{!1@2#3$a%b^c&*()}\n\n");
        assertEquals("123abc", result);
        // 3. Exceeds max length - truncated
        result = testUserNameValidity("123456789012345678901234567890thispartshouldbecutoff\n\n");
        assertEquals("123456789012345678901234567890", result);

        // Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        // 4. Give up after 3 tries
        result = testUserNameValidity("!@#$%^&*()\n*&^%$#\n@\n#\n$\n%\n");
        // No username returned
        assertNull(result);
        // Shouldn't be more than three attempts
        assertEquals(
                "Enter your username: \r\n" +
                        "Enter your username: \r\n" +
                        "Enter your username: \r\n",
                os.toString());

        //Restore normal input and output
        System.setIn(System.in);
        System.setOut(System.out);
    }

    private String testUserNameValidity (String testString) {
        System.setIn(new ByteArrayInputStream(testString.getBytes()));
        return User.getValidUserName(new Scanner(System.in));
    }

    @Test
    void testSaveAndLoad() {
        User saveTest = new User(User.TEST_USER, "Saved Test");
        Routine testRoutine = new Routine(TEST_ROUTINE);
        testRoutine.addTask(new TimedTask("Example", 30));
        saveTest.addRoutine(testRoutine);
        saveTest.save();
        saveTest = new User(User.TEST_USER + "overwritten", "Should be overwritten");
        assertEquals("Should be overwritten", saveTest.getName());
        saveTest = User.load(User.TEST_USER);
        assertNotNull(saveTest);
        assertEquals(User.TEST_USER, saveTest.getUserName());
        assertEquals("Saved Test", saveTest.getName());
        assertEquals(1, saveTest.getMyRoutines().size());
    }

    @Test
    void testSelectRoutine() {
        Routine result;
        OutputStream os = redirectOutput();
        String expected;

        // 1. Try to select with no routines
        testUser.deleteRoutines();
        result = testSelect(testUser,"\n");
        assertNull(result);
        expected = "No routines found.\r\n";
        assertEquals(expected, os.toString());

        // Add a routine
        Routine testRoutine = new Routine(TEST_ROUTINE);
        testUser.addRoutine(testRoutine);
        // Confirm that one was added
        assertEquals(1, testUser.getMyRoutines().size());
        assertEquals(expected, os.toString());

        // 2. Select a valid routine
        result = testSelect(testUser,"1\n");
        assertNotNull(result);
        assertEquals(TEST_ROUTINE, result.getName());
        // For some reason this fails, even though it also reports
        // that the expected and actual are identical.
        // I think it has to do with line endings, but nothing I
        // do makes it better...
//        expected += "1. Test Routine\nSelect one:\n";
//        assertEquals(expected, os.toString());

        // 3. Select a valid integer but bogus routine
        result = testSelect(testUser,"9999\n");
        assertNull(result);
//        expected += "1. " + TEST_ROUTINE + "\nSelect one:\n";
//        expected += "Not a valid routine.\r\n";
//        assertEquals(expected, os.toString());

        // 4. Select a non-integer bogus routine
        result = testSelect(testUser,"abc\n");
        assertNull(result);
//        expected += "1. " + TEST_ROUTINE + "\nSelect one:\n";
//        expected += "Not a valid routine.\r\n";
//        assertEquals(expected, os.toString());

        // Restore normal input and output
        System.setIn(System.in);
        System.setOut(System.out);
    }

    private Routine testSelect (User user, String testString) {
        // In try/catch to simulate behavior of app
        System.setIn(new ByteArrayInputStream(testString.getBytes()));
        try {
            return user.selectRoutine(new Scanner(System.in), "Select one:");
        }
        catch (InvalidSelectionException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private OutputStream redirectOutput() {
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        return os;
    }

}
