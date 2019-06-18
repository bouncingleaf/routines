package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
class TimedTask extends Task implements Serializable {

    // Instance variables

    private int minutes;

    // Constructors

    /**
     * Constructor for new TimedTask (always active, etc.)
     * @param name
     * @param routineID
     * @param minutes
     */
    TimedTask(String name, int routineID, int minutes) {
        super(name, routineID);
        this.minutes = minutes;
    }

    /**
     * Constructor for existing TimedTasks
     * @param id
     * @param name
     * @param routineID
     * @param minutes
     * @param active
     */
    TimedTask(int id, String name, int routineID, int minutes, boolean active) {
        super(id, name, routineID, active);
        this.minutes = minutes;
    }

    // Methods

    int getMinutes() {
        return minutes;
    }

    void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Prints the name of the task and the number of minutes followed by "min"
     */
    @Override
    public void display() {
        System.out.printf("%s\t%s\n", this.getName(), this.getTimeForDisplay());
    }

    @Override
    String getTimeForDisplay() {
        return getMinutes() + " min";
    }

}