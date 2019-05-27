package jmroy;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * User class - manages the user's info including their routines
 * Also responsible for loading/saving user data to storage.
 * @author Jessica Roy
 */
class User implements Serializable {
    
    // Instance variables
    
    private ArrayList<Routine> myRoutines;
    private String name;
    private String userName;
    private static final String FILE_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "files"
            + System.getProperty("file.separator");
    static final String USERS_FILE = FILE_DIR + "ALL_USERS.txt";

    // Constructors
    
    private User()
    {
        this.myRoutines = new ArrayList<>();
    }

    User(String userName, String name) {
        this();
        this.userName = userName;
        this.name = name.equals("") ? userName : name;
    }

    User(String userName) {
        this(userName, "");
    }

    // Class Methods
    /**
     * Prompts the user for a username, cleans the input up a bit
     * Limits the number of tries for the user (a way to exit)
     * @param input Scanner for user's input
     * @return String with the user's name, or null
     */
    static String getValidUserName(Scanner input) {
        final int MAX_USERNAME_LENGTH = 30;
        final int MAX_TRIES = 3;

        // Get the username and clean it up a bit
        // User has 3 tries to enter a username
        String userName = "";
        int tries = 0;
        while (userName.length() < 1 && tries++ < MAX_TRIES) {
            System.out.println("Enter your username: ");
            // Force lowercase, only alphanumeric, less than MAX_USERNAME_LENGTH characters
            userName = input.nextLine()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "");
            userName = userName.length() < MAX_USERNAME_LENGTH ? userName : userName.substring(0, MAX_USERNAME_LENGTH);
        }

        // If they still haven't entered anything valid, exit
        if (userName.length() < 1) {
            return null;
        }
        return userName;
    }

    // Methods

    /**
     * Gets all the routines for this user.
     * @return An ArrayList of all the user's routines.
     */
    ArrayList<Routine> getMyRoutines()
    {
        return myRoutines;
    }
    
    void addRoutine(Routine routine)
    {
        this.myRoutines.add(routine);
    }

    String getName()
    {
        return name;
    }

    String getUserName()
    {
        return userName;
    }

    /**
     * Deletes ALL routines for the user, in memory.
     * Does not save this to the file, however.
     * The caller must call save(), as the User object doesn't know its file directory.
     */
    void deleteRoutines() {
        this.myRoutines = new ArrayList<>();
    }

    /**
     * Lists all the routines for the user and all the tasks on each routine
     * Precondition: A current user must be defined.
     * Postcondition: If the user has routines, they are listed along with their tasks.
     *   If the user has no routines, a message is displayed.
     */
    void listRoutines() {
        // Get the routines
        ArrayList<Routine> routines = getMyRoutines();
        // If there aren't any, exit
        if (routines.size() == 0) {
            System.out.println("No routines found.\n");
        }
        // Otherwise, list the routines and their tasks
        else {
            System.out.println("Your routines:");
            for (Routine routine : routines) {
                routine.display();
            }
        }
    }

    /**
     * Loads a User object by reading and deserializing it from a user data file.
     * @param userName The userName of the User to load.
     * @return The User object loaded from the file.
     */
    static User load (String userName) {
        final String USER_FILE = FILE_DIR + "USER_" + userName + ".txt";
        try (FileInputStream fileInputStream = new FileInputStream(USER_FILE)) {
            ObjectInputStream objectInputStream = new ObjectInputStream((fileInputStream));
            return (User) objectInputStream.readObject();
        }
        catch (InvalidClassException e) {
            // Users should not see this...
            System.out.println("Classes have changed, try clearing the files.");
            return null;
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes the User object and saves the data to a file specific to that user.
     */
    void save () {
        System.out.print("Saving data...");
        final String USER_FILE = FILE_DIR + "USER_" + this.getUserName() + ".txt";
        try (FileOutputStream fileOutputStream = new FileOutputStream(USER_FILE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("done");
        }
        catch (IOException e) {
            System.out.println("problem saving data");
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of routines for the user and prompts for a selection.
     *
     * Precondition: A current user must be defined.
     * Precondition: input is open to a Scanner
     * @return a Routine if one is selected, null otherwise
     */
     Routine selectRoutine(Scanner input, String message) throws SelectRoutineException {
        // Get the routines
        ArrayList<Routine> routines = getMyRoutines();
        // If there aren't any, exit
        if (routines.size() == 0) {
            throw(new SelectRoutineException("No routines found."));
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
                // Valid integer, but not on the list
                throw(new SelectRoutineException("Not a valid routine."));
            }
        } catch (NumberFormatException e) {
            throw(new SelectRoutineException("Not a valid routine."));
        }
    }

}
