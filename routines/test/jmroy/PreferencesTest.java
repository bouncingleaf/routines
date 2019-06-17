package jmroy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesTest {

    @Test
    void switchStylesheets() {
        // Setup database
        Database.createDb(true);

        // Set up a test user to change the stylesheet for
        String username = User.userNamePurify(User.TEST_USER + "style");
        String name = "Stylesheets Test";
        Screen.Pages page = User.signIn(username);
        assertNotNull(User.getSignedInUser());
        if (page == Screen.Pages.NAME) {
            User.signUp(name);
        }
        assertNotNull(User.getSignedInUser());

        // Actually switch the stylesheet and confirm that it worked
        Preferences.switchStylesheets(null, Theme.DARK);
        assertEquals(Theme.DARK, User.getSignedInUser().getThemePreference());
        Preferences.switchStylesheets(Theme.DARK, Theme.LIGHT);
        assertEquals(Theme.LIGHT, User.getSignedInUser().getThemePreference());

    }
}