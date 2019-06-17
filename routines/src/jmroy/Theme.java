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
    static final Theme[] ALL_THEMES = {TEST, LIGHT, DARK, DEFAULT};

    Theme(String name, String filename, boolean show) {
        this.name = name;
        this.filename = filename;
        this.showTheme = show;
    }

    static Theme getThemeByName(String name) {
        return Database.getDb().getTheme(name);
    }

    static ArrayList<Theme> getAllThemes() {
        return Database.getDb().queryThemes();
    }

    static ArrayList<Theme> getShownThemes() {
        ArrayList<Theme> themes = getAllThemes();
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

