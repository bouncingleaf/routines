package jmroy;

import java.util.ArrayList;

/**
 * 
 * @author Jessica Roy
 *
 */
class User {
    
    // Instance variables
    
    private ArrayList<Routine> myRoutines;
    private String name;

    // Constructors
    
    User() {
        this.myRoutines = new ArrayList<>();
    }
    
    User(String name) {
        this();
        this.name = name;
    }
    
    // Methods
    
    ArrayList<Routine> getMyRoutines() {
        return myRoutines;
    }
    
    void addRoutine(Routine routine) {
        this.myRoutines.add(routine);
    }

    String getName() {
        return name;
    }

}
