package jmroy;

import java.util.ArrayList;

/**
 * 
 * @author Jessica Roy
 *
 */
public class User {
	
	// Instance variables
	
	private ArrayList<Routine> myRoutines;
	private String name;

	// Constructors
	
	User() {
		this.myRoutines = new ArrayList<Routine>();
	}
	
	User(String name) {
		this();
		this.name = name;
	}
	
	// Methods
	
	public ArrayList<Routine> getMyRoutines() {
		return myRoutines;
	}
	
	public void addRoutine(Routine routine) {
		this.myRoutines.add(routine);
	}

	public String getName() {
        return name;
    }

}
