package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
abstract class Task implements Serializable {

    // Instance variables

    private String name;
    private int id;
    private int routineID;
    private boolean active = true;

    // Constructors

    /**
     * Constructor for new Tasks
     * @param name The name of the Task
     */
    Task(String name, int routineID) {
        // This is a terrible way to assign a unique ID but it will do for now
        this.id = getNewTaskID();
        this.name = name;
        this.routineID = routineID;
    }

    /**
     * Constructor for existing Tasks where we get the id from the database
     * @param id The id of the Task
     * @param name The name of the Task
     */
    Task(int id, String name, int routineID, boolean active) {
        this.id = id;
        this.name = name;
        this.routineID = routineID;
        this.active = active;
    }

    private static int getNewTaskID() {
        return Database.getDb().getMaxTaskID() + 1;
    }

    // Methods

    abstract public void display();

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getID() {
        return id;
    }

    int getRoutineID() {
        return routineID;
    }

    @Override
    public String toString() {
        return getName() + " " + getTimeForDisplay();
    }

    abstract String getTimeForDisplay();

}
