package jmroy;

import java.io.Serializable;

/**
 * 
 * @author Jessica Roy
 *
 */
abstract class Task implements Displayable, Serializable {

    // Instance variables

    private String name;

    // Constructors

    Task(String name) {
        this.name = name;
    }

    // Methods

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName() + " " + getTimeForDisplay();
    }

    abstract String getTimeForDisplay();

}
