package jmroy;

import java.io.Serializable;

public class Theme implements Serializable {
    private String name;
    private String filename;
    static final Theme TEST = new Theme("TestName", "TestFile");
    static final Theme LIGHT = new Theme("Light", "LightTheme.css");
    static final Theme DARK = new Theme("Dark", "DarkTheme.css");
    static final Theme DEFAULT = LIGHT;
    static final Theme[] THEMES = {LIGHT, DARK};

    private Theme(String name, String filename) {
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

