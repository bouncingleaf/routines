package jmroy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    private static String dummy;
    private static final String ORIOLE = "junittest" + UUID.randomUUID();
    private static final String SPARROW = "junittest" + UUID.randomUUID();
    private static final String ROBIN = "junittest" + UUID.randomUUID();
    private static final User USER_ORIOLE = new User (ORIOLE, "Icterus Galbula");
    private static final User USER_SPARROW = new User (SPARROW, "some sparrow");
    private static final User USER_ROBIN = new User (ROBIN, "American Robin");
    private static Database db;

    @BeforeAll
    static void beforeAll() {
        dummy = "ABC";
        Database.createDb(true);
        db = Database.getDb();
    }

    @Test
    void basicTest() {
        assertEquals("ABC", dummy);
    }

    @Test
    void createDbAndGetDb() {
        assertNotNull(db);
    }

    /**
     * Tests insert, update, and upsert
     * Tests getUserIDByUsername and usernameInDatabase
     */
    @Test
    void testSetUser(){
        final String NEW_NAME = "Corvus Corax";
        // Check current size
        ArrayList<User> users = db.queryUsers();
        int currentSize = users.size();
        // Add two, checking getters
        db.insertUser(USER_ORIOLE);
        int newID = db.getUserIDByUsername(USER_ORIOLE.getUserName());
        assertNotNull(newID);
        USER_ORIOLE.setID(newID);
        db.insertUser(USER_SPARROW);
        newID = db.getUserIDByUsername(USER_SPARROW.getUserName());
        assertNotNull(newID);
        USER_SPARROW.setID(newID);
        assertTrue(db.usernameInDatabase(USER_SPARROW.getUserName()));
        // Edit using update and upsert
        USER_ORIOLE.setName(NEW_NAME);
        assertEquals(NEW_NAME, USER_ORIOLE.getName());
        db.updateUser(USER_ORIOLE);
        USER_SPARROW.setName("Another Sparrow");
        db.upsertUser(USER_SPARROW);
        // Get the users again, should be two more than before
        users = db.queryUsers();
        assertEquals(currentSize + 2, users.size());
    }

//    @Test
//    void main() {
//    }

    @Test
    void getUserByUsername() {
        assertNotNull(db);
        db.insertUser(USER_ROBIN);
        User robin = db.getUserByUsername(USER_ROBIN.getUserName());
        assertNotNull(robin);
        // This won't be true because in this test we haven't assigned the id back to the user in memory yet
//        assertEquals(robin.getID(), USER_ROBIN.getID());
        assertEquals(robin.getName(), USER_ROBIN.getName());
        assertEquals(robin.getUserName(), USER_ROBIN.getUserName());
        assertEquals(robin.getThemePreference().getName(), USER_ROBIN.getThemePreference().getName());
    }

//    @Test
//    void getTheme() {
//        assertNotNull(db);
//        Theme testTheme = db.getTheme("DEFAULT");
//        assertNotNull(testTheme);
//        assertEquals(testTheme.getName(), Theme.DEFAULT.getName());
//        assertEquals(testTheme.getFilename(), Theme.DEFAULT.getFilename());
//    }

    @Test
    void queryThemes() {
        ArrayList<Theme> query = db.queryThemes();
        // We should have at least two themes stored
        assertTrue(query.size() > 2);
    }
}