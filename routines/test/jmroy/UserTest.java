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

        // Test listRoutines
        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        testUser.listRoutines();
        assertEquals(
                "Your routines:\r\n\tRoutine: Test Routine\n",
                os.toString());

        //Restore normal output
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
        String result;
        // 1. Valid, changing capitalization
        result = testUserName("newUser\n\n");
        assertEquals("newuser", result);
        // 2. Bogus characters removed
        result = testUserName("{!1@2#3$a%b^c&*()}\n\n");
        assertEquals("123abc", result);

        //Prepare to redirect output
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);

        // 3. Give up
        result = testUserName("!@#$%^&*()\n*&^%$#\n@\n#\n$\n%\n");
        // No username returned
        assertNull(result);
        // Shouldn't be more than three attempts
        assertEquals(
                "Enter your username: \r\n" +
                        "Enter your username: \r\n" +
                        "Enter your username: \r\n",
                os.toString());

        //Restore normal output
        System.setOut(System.out);
    }

    private String testUserName (String testString) {
        System.setIn(new ByteArrayInputStream(testString.getBytes()));
        return User.getValidUserName(new Scanner(System.in));
    }

}


//    static String getValidUserName(Scanner input) {
//    void deleteRoutines() {
//    void listRoutines() {
//    static User load (String userName) {
//    void save () {
//    Routine selectRoutine(Scanner input, String message) {
