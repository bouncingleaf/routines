package jmroy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;

/**
 * 
 * @author Jessica Roy
 *
 */
class Routine implements Displayable, Serializable {

    // Class variables
    private static final Timer timer = new Timer();

    // Instance variables
    
    private String title = "My Routine";
    private ArrayList<Task> tasks;
    
    // Constructors
    
    private Routine() {
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

    /**
     * Displays a routine and lists out the current tasks and their times, in order.
     */
    public void display() {
        System.out.printf("\tRoutine: %s\n", this.getTitle());
        Task currentTask;
        // Print out the list of tasks and their times
        for (int i = 0; i < tasks.size(); i++) {
            currentTask = getTask(i);
            System.out.printf(
                "\t\t%d\t%s\t%s\n",
                i + 1,
                currentTask.getName(),
                currentTask.getTimeForDisplay()
            );
        }
    }

    private Task getTask(int i) {
        return tasks.get(i);
    }

    void run() {
        // Dummy for now
        System.out.printf("Simulation of running %s routine...\n", this.getTitle());
        Task currentTask;
        for (int i = 0; i < numberOfTasks(); i++) {
            currentTask = getTask(i);
            currentTask.display();
            if (currentTask instanceof TimedTask) {
                ((TimedTask) currentTask).countdown(timer);
            }
        }
        timer.cancel();
        System.out.println("Real functionality coming soon");
    }
}
