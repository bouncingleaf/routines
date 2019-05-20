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
    
    Routine() {
        this.tasks = new ArrayList<>();
    }

    Routine(String title) {
        this();
        this.title = title;
    }

    // Methods
    
    String getTitle() {
        return title;
    }

	// setTitle: For future use
    void setTitle(String title) {
        this.title = title;
    }

    int numberOfTasks() {
        return this.tasks.size();
    }
    
    void addTask(Task task) {
        this.tasks.add(task);
    }
    
    public void display() {
        System.out.printf("\tRoutine: %s\n", this.getTitle());
        Task currentTask;
        // Print out the list of tasks and their times
        for (int i = 0; i < tasks.size(); i++) {
            currentTask = tasks.get(i);
            System.out.printf(
                "\t\t%d\t%s\t%s\n",
                i + 1,
                currentTask.getName(),
                currentTask.getTimeForDisplay()
            );
        }
    }

    void run() {
        // Dummy for now
        System.out.printf("Simulation of running %s routine...\n", this.getTitle());
        display();
        System.out.println("Real functionality coming soon");
    }
}
