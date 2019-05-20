package jmroy;

import java.util.ArrayList;
import java.util.Scanner;

public class RoutinesApp {
    private static Scanner input;
    private static User user;

    /**
     * Prompts the user for the name of a new routine, adds one or more tasks to the 
     * new routine, adds the routine to the list of routines
     */
    private static void addNewRoutine() {
        System.out.println("Enter the name of the routine (e.g. 'My morning routine'): ");
        String routineName = input.nextLine();
        Routine newRoutine = new Routine(routineName);
        addTasksToRoutine(newRoutine);
        user.addRoutine(newRoutine);
    }

    /**
     * Prompts the user for the name and length of one or more tasks, adds the tasks to the
     * specified Routine
     * @param routine The routine to which the user wants to add tasks
     */
    private static void addTasksToRoutine(Routine routine) {
        String taskName = "ok";
        int taskLength;
        // Add tasks until the user stops
        while (!taskName.equals("")) {
            System.out.printf("\nName of task # %d (or enter, if done): \n", routine.numberOfTasks() + 1);
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
                routine.addTask(newTask);
                System.out.print("Added: ");
                newTask.display();                
            }
        }

    }

    /**
     * Lists all the routines for the specified user and all the tasks on each routine
     */
    private static void listRoutines() {
        // Get the routines
        ArrayList<Routine> routines = user.getMyRoutines();
        // If there aren't any, exit
        if (routines.size() == 0) {
            System.out.println("No routines found.\n");
        }
        // Otherwise, list the routines and prompt the user to choose a routine
        else {
            System.out.println("Your routines:");
            for (Routine routine : user.getMyRoutines()) {
                routine.display();
            }
        }
    }
    
    /**
     * Displays a list of routines for the user and prompts for a selection
     * @return a Routine if one is selected, null otherwise
     */
    private static Routine selectRoutine() {
        // Get the routines
        ArrayList<Routine> routines = user.getMyRoutines();
        // If there aren't any, exit
        if (routines.size() == 0) {
            System.out.println("No routines found.\n");
            return null;
        }
        // Otherwise, list the routines and prompt the user to choose a routine
        for (int i = 0; i < routines.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, routines.get(i).getTitle());
        }
        System.out.println("Choose a routine: ");
        int selection;
        try {
            selection = Integer.parseInt(input.nextLine());
            if (selection > 0 && selection <= routines.size()) {
                return routines.get(selection - 1);
            } else {
                // Valid integer, but not on the list? Display message
                System.out.println("Not a valid routine.");
                return null;                
            }
        } catch (NumberFormatException e) {
            System.out.println("Not a valid routine.");
            return null;
        }
    }

    /**
     * Prompts for a user's name, gets the input, sets a default if no name entered,
     * establishes the User object, and greets the user
     * @return the selected user
     */
    private static User getUser() {
        System.out.println("Enter your name: ");
        String userName = input.nextLine();
        // If the user didn't enter anything, we'll just call them "friend"
        if (userName.equals("")) {
            userName = "friend";
        }
        User newUser = new User(userName);
        System.out.printf("Hello, %s\n\n", newUser.getName());
        return newUser;
    }

    private static void mainMenuLoop() {
		// The main menu
		final String MENU =
				"1. Enter a new routine\n2. List routines\n3. Run a routine\nEnter 0 to quit.\nChoice: ";

		// Doesn't matter what choice is here as long as it isn't 0
		int choice = -1;

		while (choice != 0) {
			// Show the menu
			System.out.println(MENU);

			// Get the user's choice
			try {
				choice = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				// Just ignore non-integer input
				choice = -1;
			}

			switch (choice) {
				// 1. Add a new routine
				case 1:
					addNewRoutine();
					break;
				// 2. List the routines for that user
				case 2:
					listRoutines();
					break;
				// 3. Select a routine, run it if selected
				case 3:
					Routine selected = selectRoutine();
					if (selected != null) {
						selected.run();
					}
					break;
				// 0. Exit
				case 0:
					break;
				// Anything else, stay in the loop
				default:
					break;
			}
		}
	}

    /**
     * The main method of the program. Loops on displaying a menu and accepting and handling
     * a menu choice from the user, until the user chooses to quit the application.
     * @param args command line arguments, not expecting any
     */
    public static void main(String[] args) {
        // Scanner for getting user input
        input = new Scanner(System.in);

        // Display an application title
        System.out.println("Routines!\n");
        
        // Get the user whose routines we want to work with
        user = getUser(); 

        // Display the menu, get the user's choice, handle it
        mainMenuLoop();

        // The user has opted to quit. Close the input and say goodbye.
        input.close();
        System.out.printf("Done. See you next time, %s!\n\n", user.getName());
    }

}
