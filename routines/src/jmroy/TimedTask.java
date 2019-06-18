package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
class TimedTask extends Task implements Serializable {

    // Instance variables

    private int minutes;

    // Constructors

    TimedTask(String name, long routineID, int minutes) {
        super(name, routineID);
        this.minutes = minutes;
    }

    TimedTask(long id, String name, long routineID, int minutes) {
        super(id, name, routineID);
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