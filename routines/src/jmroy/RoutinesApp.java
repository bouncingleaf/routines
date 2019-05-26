package jmroy;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RoutinesApp {
    private static Scanner input;
    private static User user;
    private static final String FILE_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "files"
            + System.getProperty("file.separator");
    private static final String USERS_FILE = FILE_DIR + "ALL_USERS.txt";

    /**
     * Prompts the user for the name of a new routine, adds one or more tasks to the
     * new routine, adds the routine to the list of routines
     *
     * Precondition: input is open to a Scanner
     * Postcondition: A new Routine is created for the current User
     */
    private static void addNewRoutine() {
        System.out.println("Enter the name of the routine (e.g. 'My morning routine'): ");
        String routineName = input.nextLine();
        Routine newRoutine = new Routine(routineName);
        newRoutine.addTasksToRoutine(input);
        user.addRoutine(newRoutine);
        user.save(FILE_DIR);
    }

    /**
     * Displays a list of routines for the user and prompts for a selection.
     *
     * Precondition: A current user must be defined.
     * Precondition: input is open to a Scanner
     * @return a Routine if one is selected, null otherwise
     */
    private static Routine selectRoutine(String message) {
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
        System.out.println(message);
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
     * Prompts for a user's name, gets the input, looks up the user in the
     * users file. If found, loads the user's data. If not found, starts a
     * new user. Either way (unless there's an error or no username entered),
     * establishes the User object, and greets the user.
     *
     * Precondition: input is open to a Scanner
     * @return the selected user
     */
    private static User getUser(Scanner userReader, PrintWriter userWriter) {
        final int MAX_USERNAME_LENGTH = 30;
        User newUser;
        // Get the username and clean it up a bit
        // User has 3 tries to enter a username
        String userName = "";
        int tries = 0;
        while (userName.length() < 1 && tries++ < 3) {
            System.out.println("Enter your username: ");
            // Force lowercase, only alphanumeric, less than MAX_USERNAME_LENGTH characters
            userName = input.nextLine()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]","");
            userName = userName.length() < MAX_USERNAME_LENGTH ? userName : userName.substring(0, MAX_USERNAME_LENGTH);
        }

        // If they still haven't entered anything valid, exit
        if (userName.length() < 1) {
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
            newUser = User.load(FILE_DIR, userName);
        } else {
            System.out.println("Greetings new user. Enter your name (optional): ");
            // Not the best sanitizing but it'll do
            String name = input.nextLine().replaceAll("[^a-zA-Z0-9\']"," ");
            newUser = new User(userName, name);

            // Save username to the users file
            userWriter.println(newUser.getUserName());

            // Initial save of user data
            newUser.save(FILE_DIR);
        }

        if (newUser != null) {
            System.out.printf("Hello, %s\n\n", newUser.getName());
        }
        return newUser;
    }

    /**
     * The main menu loop for the program. Displays the menu, gets the user's choice,
     * and handles the user's choice accordingly. Loop continues until the user
     * chooses to exit.
     *
     * Precondition: input is open to a Scanner
     */
    private static void mainMenuLoop() {
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
				choice = Integer.parseInt(input.nextLine());
                switch (choice) {
                    case 1:
                        addNewRoutine();
                        break;
                    case 2:
                        user.listRoutines();
                        break;
                    case 3:
                        Routine routineToEdit = selectRoutine("Choose a routine to edit: ");
                        if (routineToEdit != null) {
                            routineToEdit.edit();
                            user.save(FILE_DIR);
                        }
                        break;
                    case 4:
                        Routine routineToRun = selectRoutine("Choose a routine to run: ");
                        if (routineToRun != null) {
                            routineToRun.run();
                        }
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
     * The main method of the program. Loops on displaying a menu and accepting and handling
     * a menu choice from the user, until the user chooses to quit the application.
     *
     * @param args command line arguments, not expecting any
     */
    public static void main(String[] args) {
        // Scanner for getting user input
        input = new Scanner(System.in);

        // Display an application title
        System.out.println("Routines!\n");

        // Get the user whose routines we want to work with
        File usersFile = new File(USERS_FILE);
        // Establish readers and writers to the users file, and go get the user
        try (
                Scanner userReader = usersFile.exists() ? new Scanner(usersFile) : null;
                PrintWriter userWriter = new PrintWriter(new FileOutputStream(usersFile, true))
            ) {
            user = getUser(userReader, userWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            user = null;
        }

        if (user == null) {
            // Something went wrong
            System.out.println("Goodbye.");
        } else {
            // Display the menu, get the user's choice, handle it
            mainMenuLoop();

            // The user has opted to quit. Close the input and say goodbye.
            System.out.printf("Done. See you next time, %s!\n\n", user.getName());
        }
        input.close();
        System.exit(0);
    }

}
