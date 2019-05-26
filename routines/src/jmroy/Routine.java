package jmroy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Jessica Roy
 *
 */
class Routine implements Displayable, Serializable {

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

    void setTitle(String title) {
        this.title = title;
    }

    int numberOfTasks() {
        return this.tasks.size();
    }
    
    void addTask(Task task) {
        this.tasks.add(task);
    }

    private Task getTask(int i) {
        return tasks.get(i);
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
        System.out.printf("\tRoutine: %s\n", this.getTitle());
        Task currentTask;
        // Print out the list of tasks and their times
        for (int i = 0; i < numberOfTasks(); i++) {
            currentTask = getTask(i);
            System.out.printf(
                "\t\t%d\t%s\t%s\n",
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
        Integer taskNumber = chooseATask(input, "Choose a task to edit");
        if (taskNumber != null) {
            Task taskToEdit = getTask(taskNumber);
            System.out.printf("New name (enter to keep \"%s\"):\n", taskToEdit.getName());
            String newName = input.nextLine();
            if (newName.length() < 1) {
                newName = taskToEdit.getName();
            }
            System.out.printf("New time (or 0 for untimed, or enter to keep %s):\n", taskToEdit.getTimeForDisplay());
            int newTime;
            try {
                newTime = Integer.parseInt(input.nextLine());
            }
            catch (Exception e) {
                newTime = 0;
            }
            Task newTask;
            if (newTime > 0) {
                newTask = new UntimedTask(newName);
            } else {
                newTask = new TimedTask(newName, newTime);
            }
            setTask(taskNumber, newTask);
        } else {
            System.out.println("No changes made.");
        }
    }

    /**
     * Prompts the user to choose a task to delete.
     * If one is chosen, deletes the task and displays the info deleted.
     * If none is chosen, displays a message.
     * @param input The Scanner for getting user input
     */
    private void editDeleteTask(Scanner input) {
        Integer taskNumber = chooseATask(input, "Choose a task to delete");
        if (taskNumber != null) {
            Task taskToDelete = getTask(taskNumber);
            deleteTask(taskNumber);
            System.out.printf("Deleted task %s\t%s\n", taskToDelete.getName(), taskToDelete.getTimeForDisplay());
        } else {
            System.out.println("No task deleted.");
        }
    }

    /**
     * Prompts the user to choose a task from the routine
     * If a valid task is selected, the index number of the task is returned
     * Otherwise, a message is displayed and null is returned
     * @param input The Scanner for getting user input
     * @param message The message to display with the prompt, e.g.
     *                "Choose a task to delete"
     * @return The index # of the selected task, or null if none is selected
     */
    private Integer chooseATask(Scanner input, String message) {
        System.out.printf("%s (choose 1 - %d):\n", message, numberOfTasks());
        int selection;
        try {
            selection = Integer.parseInt(input.nextLine());
            if (selection > 0 && selection <= numberOfTasks()) {
                return selection - 1;
            } else {
                // Valid integer, but not on the list? Display message
                System.out.println("Not a valid task.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Not a valid task.");
            return null;
        }
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

        display();
        while (!done) {
            // Show the menu
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
                int count = 0;
                for (int j = ((TimedTask) currentTask).getMinutes(); j > 0; j--) {
                    System.out.printf("%02d minutes", j);
                    System.out.print(count++%10 == 0 ? "\n" : "...");
                }
            } else {
                System.out.println("Untimed!");
            }
        }
        System.out.println("Real functionality coming soon");
    }
}
