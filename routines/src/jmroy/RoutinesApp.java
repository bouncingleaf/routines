package jmroy;

import java.io.*;
import java.util.Scanner;

/**
 * RoutinesApp - main entry point for the Routines application
 * @author Jessica Roy
 */
public class RoutinesApp {
    private static User user;

    /**
     * Prompts the user for the name of a new routine, adds one or more tasks to the
     * new routine, adds the routine to the list of routines
     *
     * Precondition: input is open to a Scanner
     * Postcondition: A new Routine is created for the current User
     */
    private static Routine createNewRoutine(Scanner input) {
        System.out.println("Enter the name of the routine (e.g. 'My morning routine'): ");
        String routineName = input.nextLine();
        if (routineName.length() > 0 ) {
            Routine newRoutine = new Routine(routineName);
            newRoutine.addTasksToRoutine(input);
            return newRoutine;
        } else {
            return null;
        }
    }

    /**
     * Prompts for a user's username, gets the input, looks up the user in the
     * users file. If found, loads the user's data. If not found, starts a
     * new user. Either way (unless there's an error or no username entered),
     * establishes the User object, and greets the user.
     * @param input Scanner for the user's input
     * @return the selected User, or null if one is not selected
     */
    private static User getUser(Scanner input) {
        // Get the user whose routines we want to work with
        File usersFile = new File(User.USERS_FILE);
        // Establish readers and writers to the users file, and go get the user
        try (
                Scanner userReader = usersFile.exists() ? new Scanner(usersFile) : null;
                PrintWriter userWriter = new PrintWriter(new FileOutputStream(usersFile, true))
        ) {
            User newUser;
            String userName = User.getValidUserName(input);
            if (userName == null) {
                return null;
            }
            // Look for the username in the users file
            boolean found = false;
            if (userReader != null) {
                while (userReader.hasNextLine() && !found) {
                    found = userReader.nextLine().equals(userName);
                }
            }

            if (found) {
                newUser = User.load(userName);
            } else {
                System.out.println("Greetings new user. Enter your name (optional): ");

                // Not the best sanitizing but it'll do
                String name = input.nextLine().replaceAll("[^a-zA-Z0-9\']"," ");
                newUser = new User(userName, name);

                // Save username to the users file
                userWriter.println(newUser.getUserName());

                // Initial save of user data
                newUser.save();
            }

            if (newUser != null) {
                System.out.printf("Hello, %s\n\n", newUser.getName());
            }
            return newUser;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * The main menu loop for the program. Displays the menu, gets the user's choice,
     * and handles the user's choice accordingly. Loop continues until the user
     * chooses to exit.
     * @param input Scanner for the user's input
     */
    private static void mainMenuLoop(Scanner input) {
		// The main menu
		final String MENU =
                "\nMAIN MENU\n" +
				"1. Enter a new routine\n" +
                "2. List routines\n" +
                "3. Edit a routine\n" +
                "4. Run a routine\n" +
                "Anything else to quit.\n" +
                "Choice: ";

		boolean done = false;
		int choice;

		while (!done) {
			// Show the menu
			System.out.println(MENU);

			// Get the user's choice
			try {
                String inputValue = input.nextLine();
                if (inputValue.length() == 0) {
                    done = true;
                } else {
                    choice = Integer.parseInt(inputValue);
                    switch (choice) {
                        case 1:
                            Routine newRoutine = createNewRoutine(input);
                            if (newRoutine != null) {
                                user.addRoutine(newRoutine);
                                user.save();
                            }
                            break;
                        case 2:
                            user.listRoutines();
                            break;
                        case 3:
                            try {
                                Routine routineToEdit = user.selectRoutine(input, "Choose a routine to edit: ");
                                routineToEdit.edit(input);
                                user.save();
                            }
                            catch (SelectRoutineException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 4:
                            try {
                                Routine routineToRun = user.selectRoutine(input, "Choose a routine to run: ");
                                routineToRun.run();
                                user.save();
                            }
                            catch (SelectRoutineException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        default:
                            done = true;
                    }
                }
			} catch (NumberFormatException e) {
				// Exit for non-integer input
                done = true;
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
        Scanner input = new Scanner(System.in);

        // Display an application title
        System.out.println("Routines!\n");

        // Get the user
        user = getUser(input);

        if (user == null) {
            // Something went wrong
            System.out.println("Goodbye.");
        } else {
            // Display the menu, get the user's choice, handle it
            mainMenuLoop(input);

            // The user has opted to quit. Close the input and say goodbye.
            System.out.printf("Done. See you next time, %s!\n\n", user.getName());
        }
        input.close();
        System.exit(0);
    }

}
