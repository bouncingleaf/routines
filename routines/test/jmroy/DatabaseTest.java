package jmroy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    private static Database db;
    private static String dummy;
    private static final User USER_ONE = new User ("oriole", "Icterus Galbula");
    private static final User USER_TWO = new User ("sparrow", "some sparrow");

    @BeforeAll
    static void beforeAll() {
        dummy = "ABC";
        Database.createDb(true);
        db = Database.getDb();
    }

    @Test
    void testGetUser(){
        assertEquals("ABC", dummy);
        ArrayList<User> users = db.queryUsers();
        assertEquals(0, users.size());
        db.insertUser(USER_ONE);
        db.insertUser(USER_TWO);
        users = db.queryUsers();
        assertEquals(1, users.size());
        User firstResult = users.get(0);
        assertEquals(firstResult.getName(), USER_ONE.getName());
        assertEquals(firstResult.getUserName(), USER_ONE.getUserName());
        assertEquals(firstResult.getId(), USER_ONE.getId());
        assertEquals(firstResult.getThemePreference(), USER_ONE.getThemePreference());
    }

    @Test
    void testSetUser(){
        assertEquals("ABC", dummy);
        db.insertUser(USER_ONE);
        db.insertUser(USER_TWO);
        USER_ONE.setName("Corvus Corax");
        db.updateUser(USER_ONE);

    }
}