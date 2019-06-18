package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
class UntimedTask extends Task implements Serializable {

    // Constructors

    /**
     * Constructor for new UntimedTask
     * @param name
     * @param routineID
     */
    UntimedTask(String name, int routineID) {
        super(name, routineID);
    }

    /**
     * Constructor for existing TimedTask
     * @param id
     * @param name
     * @param routineID
     * @param active
     */
    UntimedTask(int id, String name, int routineID, boolean active) {
        super(id, name, routineID, active);
    }

    // Methods
    
    @Override
    public void display() {
        System.out.printf("%s\t%s\n", this.getName(), this.getTimeForDisplay());
    }
    
    @Override
    String getTimeForDisplay() {
        return "(untimed)";
    }

}