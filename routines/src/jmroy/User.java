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

    // Class variables and constants
    private static User signedInUser;
    private static final String FILE_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "files"
            + System.getProperty("file.separator");

    private static final String USERS_FILE = FILE_DIR + "ALL_USERS";
    // Usernames may not begin with this string - reserved for unit testing
    static final String TEST_USER = "junittest";


    // Instance variables

    private ArrayList<Routine> myRoutines;
    private String name;
    private String userName;
    private Theme themePreference = Theme.DEFAULT;

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

    private static void setSignedInUser(User user) {
        signedInUser = user;
    }
    static User getSignedInUser() {
        return signedInUser;
    }

    static User createNewUser(String userName) {
        File usersFile = new File(User.USERS_FILE);
        try (
                PrintWriter userWriter = new PrintWriter(new FileOutputStream(usersFile, true))
        ) {
            User newUser = new User(userName);

            // Save username to the users file
            userWriter.println(newUser.getUserName());

            newUser.save();
            return newUser;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String userNamePurify(String name){
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    static Screen.Pages signIn(String nameInput) {
        String name = userNamePurify(nameInput);
        if (name.length() > 0) {
            if (userFound(name)) {
                setSignedInUser(User.load(name));
                return Screen.Pages.MAIN;
            } else {
                setSignedInUser(User.createNewUser(name));
                return Screen.Pages.NAME;
            }
        } else {
            // Something has gone wrong, we shouldn't get here
            System.out.println("Sign in not valid");
            return Screen.Pages.ERROR;
        }
    }

    static void signUp(String nameInput) {
        String name = nameInput.replaceAll("[^a-zA-Z0-9\']"," ");
        getSignedInUser().setName( name.length() > 0 ? name : User.getSignedInUser().getUserName());
        getSignedInUser().save();
    }


    /**
     * Get or create the User whose routines we want to work with
     * @param userName String of the user name to look for
     * @return true if the user is found, false if not
     */
     static boolean userFound(String userName) {
        // Open the users file, establish a reader, and get the user
        File usersFile = new File(User.USERS_FILE);
        try (
                Scanner userReader = usersFile.exists() ? new Scanner(usersFile) : null
        ) {
            boolean found = false;
            // If we have a username, look for it in the users file
            if (userName != null && userReader != null) {
                while (userReader.hasNextLine() && !found) {
                    found = userReader.nextLine().equals(userName);
                }
            }
            return found;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads a User object by reading and deserializing it from a user data file.
     * @param userName The userName of the User to load.
     * @return The User object loaded from the file.
     */
    static User load (String userName) {
        final String USER_FILE = FILE_DIR + "USER_" + userName;
        try (FileInputStream fileInputStream = new FileInputStream(USER_FILE)) {
            ObjectInputStream objectInputStream = new ObjectInputStream((fileInputStream));
            return (User) objectInputStream.readObject();
        }
        catch (InvalidClassException e) {
            // Users should not see this...
            System.out.println("Classes have changed, try clearing the files.");
            return null;
        }
        catch (FileNotFoundException e) {
            System.out.println("Uh oh, no file found for this user " + userName);
            return null;
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getStylesheet() {
        return getSignedInUser() == null ? Theme.DEFAULT.getFilename() :
                getSignedInUser().getThemePreference().getFilename();
    }

    // Instance Methods

    /**
     * Gets all the routines for this user.
     * @return An ArrayList of all the user's routines.
     */
    ArrayList<Routine> getMyRoutines()
    {
        return myRoutines;
    }

    void setMyRoutines(ArrayList<Routine> myRoutines) {
        this.myRoutines = myRoutines;
    }

    void addRoutine(Routine routine)
    {
        myRoutines.add(routine);
    }

    String getName()
    {
        return name;
    }

    void setName(String name) {
        this.name = name;
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
     * Serializes the User object and saves the data to a file specific to that user.
     */
    void save () {
        final String USER_FILE = FILE_DIR + "USER_" + this.getUserName();
        try (FileOutputStream fileOutputStream = new FileOutputStream(USER_FILE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (IOException e) {
            System.out.println("problem saving data");
            e.printStackTrace();
        }
    }

    Theme getThemePreference() {
        return themePreference;
    }

    void setThemePreference(Theme themePreference) {
        this.themePreference = themePreference;
    }

}
