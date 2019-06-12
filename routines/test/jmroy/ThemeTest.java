package jmroy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThemeTest {

    @Test
    void getName() {
        assertEquals("TestName", Theme.TEST.getName());
    }

    @Test
    void getFilename() {
        assertEquals("TestFile", Theme.TEST.getFilename());
    }

    @Test
    void toStringTheme() {
        assertEquals("TestName", Theme.TEST.toString());
    }
}