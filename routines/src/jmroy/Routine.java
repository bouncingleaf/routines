package jmroy;

import java.util.ArrayList;

/**
 * 
 * @author Jessica Roy
 *
 */
class Routine implements Displayable {
	
    // Instance variables
	
	private String title = "My Routine";
	private ArrayList<Task> tasks;
	
	// Constructors
	
	public Routine() {
		this.tasks = new ArrayList<Task>();
	}

	public Routine(String title) {
		this();
		this.title = title;
	}

	// Methods
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int numberOfTasks() {
		return this.tasks.size();
	}
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	public void display() {
		System.out.printf("\tRoutine: %s\n", this.getTitle());
		Task currentTask;
		for (int i = 0; i < tasks.size(); i++) {
		    currentTask = tasks.get(i);
			System.out.printf(
			    "\t\t%d\t%s\t%s\n",
			    i,
			    currentTask.getName(),
			    currentTask.getTimeForDisplay()
			);
		}
	}

	public void run() {
	    // Dummy for now
		System.out.printf("Simulation of running %s routine...\n", this.getTitle());
		display();
		System.out.println("Real functionality coming soon");
	}
}
