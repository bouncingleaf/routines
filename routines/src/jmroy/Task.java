package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
abstract class Task implements Serializable {

    // Instance variables

    private String name;
    private long id;
    private long routineID;

    // Constructors

    /**
     * Constructor for new Tasks
     * @param name The name of the Task
     */
    Task(String name, long routineID) {
        // This is a terrible way to assign a unique ID but it will do for now
        this.id = System.currentTimeMillis();
        this.name = name;
        this.routineID = routineID;
    }

    /**
     * Constructor for existing Tasks where we get the id from the database
     * @param id The id of the Task
     * @param name The name of the Task
     */
    Task(long id, String name, long routineID) {
        this.id = id;
        this.name = name;
        this.routineID = routineID;
    }

    // Methods

    abstract public void display();

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    long getID() {
        return id;
    }

    long getRoutineID() {
        return routineID;
    }

    @Override
    public String toString() {
        return getName() + " " + getTimeForDisplay();
    }

    abstract String getTimeForDisplay();

}
