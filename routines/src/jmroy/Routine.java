package jmroy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Routine class: a Routine is a list of tasks, with a title.
 * @author Jessica Roy
 */
class Routine implements Selectable, Serializable {

    // Class constants
    static final String SINGULAR = "routine";
    static final String PLURAL = "routines";

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

    // For Selectable interface
    public String getName() {
        return title;
    }

    String getTitle() { return title; }

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
     * Get Task object by index number (0, 1, 2...)
     * @param i The index of the task to get
     * @return the Task at that index
     */
    private Task getTask(int i) {
        return tasks.get(i);
    }

    private ArrayList<Task> getTasks() { return tasks; }

    /**
     * Get task description by task number (1, 2, 3...)
     * @param i The number of the task to get
     * @return A string describing the task
     */
    String getTaskByIndex(int i) {
        return tasks.get(i - 1).toString();
    }
    
    private void setTask(int i, Task task) {
        tasks.set(i, task);
    }

    private void deleteTask(int i) {
        tasks.remove(i);
    }

    /**
     * Displays a routine and lists out the current tasks and their times, in order.
     */
    public void display() {
        System.out.printf("%s\n", this.getTitle());
        Task currentTask;
        // Print out the list of tasks and their times
        for (int i = 0; i < numberOfTasks(); i++) {
            currentTask = getTask(i);
            System.out.printf(
                "\t%d\t%s\t%s\n",
                i + 1,
                currentTask.getName(),
                currentTask.getTimeForDisplay()
            );
        }
    }

    /**
     * Prompts the user for a new title for the routine and sets it
     * @param input The Scanner for getting user input
     */
    private void changeRoutineName(Scanner input) {
        System.out.printf("Rename routine \"%s\" to (enter to skip):\n", getTitle());
        String newTitle = input.nextLine();
        if (newTitle.length() > 0) {
            setTitle(newTitle);
        }
    }

    /**
     * Prompts the user for the name and length of one or more tasks, adds the tasks to the
     * specified Routine
     * Precondition: Zero or more Tasks are added to the provided Routine
     * @param input The Scanner for getting user input
     */
     void addTasksToRoutine(Scanner input) {
        String taskName = "ok";
        int taskLength;
        // Add tasks until the user stops adding
        while (!taskName.equals("")) {
            System.out.printf("\nName of task # %d (or enter, if done): \n", numberOfTasks() + 1);
            taskName = input.nextLine();
            if (taskName.length() > 0) {
                System.out.println("Length of task (in minutes), or enter for untimed task: ");
                try {
                    taskLength = Integer.parseInt(input.nextLine());
                }
                catch (NumberFormatException e) {
                    taskLength = 0;
                }
                Task newTask = taskLength > 0 ? new TimedTask(taskName, taskLength) : new UntimedTask(taskName);
                addTask(newTask);
                System.out.print("Added: ");
                newTask.display();
            }
        }
    }

    /**
     * Prompts the user to choose a task to edit.
     * If one is chosen, allows the user to enter a new name and/or time.
     * If none is chosen, displays a message.
     * @param input The Scanner for getting user input
     */
    private void editModifyTask(Scanner input) {
        try {
            int taskIndex = selectTask(input, "Choose a task to edit");
            Task taskToEdit = getTask(taskIndex);

            // Prompt for the new name
            System.out.printf("New name (enter to keep \"%s\"):\n", taskToEdit.getName());
            String newName = input.nextLine();
            if (newName.length() < 1) {
                newName = taskToEdit.getName();
            }

            // Prompt for the new time
            if (taskToEdit instanceof UntimedTask) {
                System.out.println("New time, or enter to keep untimed:");
            } else {
                System.out.printf("New time, enter to keep %s, 0 for untimed:\n", taskToEdit.getTimeForDisplay());
            }

            // Get the new time, if any
            try {
                String inputValue = input.nextLine();
                if (inputValue.length() == 0) {
                    // No new time - just update the name
                    taskToEdit.setName(newName);
                } else {
                    // New time was specified, set the new task
                    int newTime = Integer.parseInt(inputValue);
                    if (newTime > 0) {
                        setTask(taskIndex, new TimedTask(newName, newTime));
                    } else {
                        setTask(taskIndex, new UntimedTask(newName));
                    }
                }
            }
            catch (Exception e) {
                // No time changes if input not an integer
                taskToEdit.setName(newName);
            }
        }
        catch (InvalidSelectionException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prompts the user to choose a task to delete.
     * If one is chosen, deletes the task and displays the info deleted.
     * If none is chosen, displays a message.
     * @param input The Scanner for getting user input
     */
    private void editDeleteTask(Scanner input) {
        try {
            int taskIndex = selectTask(input, "Choose a task to delete");
            Task taskToDelete = getTask(taskIndex);
            deleteTask(taskIndex);
            System.out.printf("Deleted task %s\t%s\n", taskToDelete.getName(), taskToDelete.getTimeForDisplay());
        }
        catch (InvalidSelectionException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Selects a task from the routine's list of tasks
     * @param input Scanner for user input
     * @param message Message to display before selection is made
     * @return index of selected task, if one is selected
     * @throws InvalidSelectionException if no valid selection is made
     */
    private Integer selectTask(Scanner input, String message) throws InvalidSelectionException {
        return new Selection<Task>().selectItem(input, getTasks(), message, Task.SINGULAR, Task.PLURAL);
    }

    /**
     * Displays the edit menu, gets the user's choice, and switches
     * to the correct routine to handle the choice.
     */
    void edit(Scanner editInput) {
        final String EDIT_MENU =
                        "1. Change the routine name\n" +
                        "2. Add a task\n" +
                        "3. Edit a task\n" +
                        "4. Delete a task\n" +
                        "Anything else when done.\n" +
                        "Choice: ";
        boolean done = false;
        int choice;

        while (!done) {
            // Show the menu
            System.out.print("\nManage Routine: ");
            this.display();
            System.out.println(EDIT_MENU);

            // Get the user's choice
            try {
                choice = Integer.parseInt(editInput.nextLine());
                switch (choice) {
                    case 1:
                        changeRoutineName(editInput);
                        break;
                    case 2:
                        addTasksToRoutine(editInput);
                        break;
                    case 3:
                        editModifyTask(editInput);
                        break;
                    case 4:
                        editDeleteTask(editInput);
                        break;
                    default:
                        done = true;
                }
            } catch (NumberFormatException e) {
                // Exit for non-integer input
                done = true;
            }
        }
     }

    /**
     * For now, a simulation of running the routine
     */
    void run() {
        System.out.printf("Simulation of running %s routine...\n", this.getTitle());
        Task currentTask;
        for (int i = 0; i < numberOfTasks(); i++) {
            currentTask = getTask(i);
            System.out.println();
            currentTask.display();
            if (currentTask instanceof TimedTask) {
                int count = 1;
                for (int j = ((TimedTask) currentTask).getMinutes(); j > 0; j--) {
                    System.out.printf("%02d:00", j);
                    System.out.print(count++%10 == 0 ? "...\n" : "...");
                }
                System.out.println("Timer done");
            } else {
                System.out.println("Untimed!");
            }
        }
        System.out.println("\nReal functionality coming soon");
    }
}
