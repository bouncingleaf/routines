package jmroy;

import java.io.Serializable;

public class Theme implements Serializable {
    private String name;
    private String filename;
    static final Theme DARK = new Theme("dark", "DarkTheme.css");
    static final Theme LIGHT = new Theme("light", "LightTheme.css");
    static final Theme[] THEMES = {DARK, LIGHT};

    Theme(String name, String filename) {
        this.name = name;
        this.filename = filename;
    }

    String getName()
    {
        return name;
    }

    String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return name;
    }
}

