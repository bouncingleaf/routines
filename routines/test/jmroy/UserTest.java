package jmroy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.UUID;

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

        // Test adding a routine
        Routine testRoutine = new Routine(TEST_ROUTINE);
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

        // Test user constructed with two arguments, second argument non-empty
        assertEquals(USER, testUser.getUserName());
        assertEquals(NAME, testUser.getName());

        // Test user constructed with second argument empty
        // userName should equal name
        User testTwo = new User(USER_TWO, "");
        assertEquals(USER_TWO, testTwo.getUserName());
        assertEquals(USER_TWO, testTwo.getName());
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
    void testThemePreference() {
        User themeTest = new User(User.TEST_USER, "Theme Test");
        themeTest.setThemePreference(Theme.DARK);
        assertEquals(Theme.DARK, themeTest.getThemePreference());
        themeTest.setThemePreference(Theme.LIGHT);
        assertEquals(Theme.LIGHT, themeTest.getThemePreference());
    }

    @Test
    void testSignUpSignIn() {
        String username = User.userNamePurify(User.TEST_USER + "SignInTest");
        String name = "Signed Up";
        Screen.Pages page = User.signIn(username);
        assertNotNull(User.getSignedInUser());
        assertEquals(username, User.getSignedInUser().getUserName());
        if (page == Screen.Pages.NAME) {
            User.signUp(name);
        }
        assertEquals(username, User.getSignedInUser().getUserName());
        assertEquals(name, User.getSignedInUser().getName());
    }

    @Test
    void testCreateAndFound() {
        String username = User.userNamePurify(User.TEST_USER + UUID.randomUUID());
        User newUser = new User(username, username);
        assertNotNull(newUser);
        assertEquals(username, newUser.getUserName());
    }
}
