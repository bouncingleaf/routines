package jmroy;

import java.io.Serializable;
import java.util.ArrayList;

public class Theme implements Serializable {
    private String name;
    private String filename;
    private boolean showTheme;
    static final Theme TEST = new Theme("TestName", "TestFile", false);
    static final Theme LIGHT = new Theme("Light", "LightTheme.css", true);
    static final Theme DARK = new Theme("Dark", "DarkTheme.css", true);
    static final Theme DEFAULT = new Theme("Default", "LightTheme.css", false);

    Theme(String name, String filename, boolean show) {
        this.name = name;
        this.filename = filename;
        this.showTheme = show;
    }

//    static void addThemesToDatabase() {
//        Database.getDb().insertTheme(TEST);
//        Database.getDb().insertTheme(LIGHT);
//        Database.getDb().insertTheme(DARK);
//        Database.getDb().insertTheme(DEFAULT);
//    }

    static Theme getThemeByName(String name) {
        return Database.getDb().getTheme(name);
    }

    static ArrayList<Theme> getShowableThemes() {
        ArrayList<Theme> themes = Database.getDb().queryThemes();
        themes.removeIf(Theme::getShowTheme);
        return themes;
    }

    String getName()
    {
        return name;
    }

    String getFilename() {
        return filename;
    }

    boolean getShowTheme() { return showTheme; }

    @Override
    public String toString() {
        return name;
    }
}

