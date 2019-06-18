package jmroy;

import java.io.Serializable;

/**
 * @author Jessica Roy
 */
class UntimedTask extends Task implements Serializable {

    // Constructors
    
    UntimedTask(String name, long routineID) {
        super(name, routineID);
    }

    UntimedTask(long id, String name, long routineID) {
        super(id, name, routineID);
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