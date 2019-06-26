package jmroy;

import java.io.*;
import java.util.ArrayList;

/**
 * User class - manages the user's info including their routines
 * Also responsible for loading/saving user data to storage.
 *
 * @author Jessica Roy
 */
class User implements Serializable {

    // Class variables and constants
    private static User signedInUser;
    private static final String FILE_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "files"
            + System.getProperty("file.separator");

    // Usernames may not begin with this string - reserved for unit testing
    static final String TEST_USER = "junittest";


    // Instance variables

    private int id;
    private ArrayList<Routine> myRoutines;
    private ArrayList<Report> myReports;
    private String name;
    private String userName;
    private Theme themePreference = Theme.DEFAULT;

    // Constructors

    /**
     * Constructor for building a new user with the default theme
     *
     * @param userName The user's chosen username
     * @param name     The user's chosen name, or empty string to just use username
     */
    User(String userName, String name) {
        this.myRoutines = new ArrayList<>();
        this.myReports = new ArrayList<>();
        this.userName = userName;
        this.name = name.equals("") ? userName : name;
    }

    /**
     * Constructor for loading existing an existing user from the database
     *
     * @param id       The user's already existing ID
     * @param userName The user's chosen username
     * @param name     The user's chosen name, or empty string to just use username
     * @param theme    The user's chosen theme
     */
    User(int id, String userName, String name, Theme theme) {
        this(userName, name);
        this.id = id;
        this.themePreference = theme;
    }
    // Class Methods

    private static void setSignedInUser(User user) {
        signedInUser = user;
    }

    static User getSignedInUser() {
        return signedInUser;
    }

    /**
     * Create a new user with the given username
     * @param userName The username to use
     * @return The User object created
     */
    static User createNewUser(String userName) {
        User newUser = new User(userName, "");

        // Save user object to database
        Database.getDb().insertUser(newUser);

        // File ID back to user in memory
        Integer newID = Database.getDb().getUserIDByUsername(newUser.getUserName());
        if (newID == null) {
            System.out.println("problem getting id");
        } else {
            newUser.setID(newID);
        }

        // Save user's own personal data file
        newUser.saveUserDataFile();

        return newUser;
    }

    static String userNamePurify(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    static Screen.Pages signIn(String nameInput) {
        String name = userNamePurify(nameInput);
        if (name.length() > 0) {
            if (userFound(name)) {
                User loadedUser = User.loadFromDataFile(name);
                System.out.println("Setting to loaded user: " + loadedUser);
                setSignedInUser(loadedUser);
                return Screen.Pages.MAIN;
            } else {
                User createdUser = User.createNewUser(name);
                System.out.println("Setting to created user: " + createdUser);
                setSignedInUser(createdUser);
                return Screen.Pages.NAME;
            }
        } else {
            // Something has gone wrong, we shouldn't get here
            System.out.println("Sign in not valid");
            return Screen.Pages.ERROR;
        }
    }

    static void signUp(String nameInput) {
        String name = nameInput.replaceAll("[^a-zA-Z0-9\']", " ");
        getSignedInUser().setName(name.length() > 0 ? name : User.getSignedInUser().getUserName());
        getSignedInUser().saveUserDataFile();
    }

    static boolean userFound(String username) {
        return Database.getDb().usernameInDatabase(username);
    }

    /**
     * Loads a User object by reading and deserializing it from a user data file.
     *
     * @param userName The userName of the User to load.
     * @return The User object loaded from the file.
     */
    static User loadFromDataFile(String userName) {
        System.out.println("Loading " + userName);
        final String USER_FILE = FILE_DIR + "USER_" + userName;
        try (FileInputStream fileInputStream = new FileInputStream(USER_FILE)) {
            ObjectInputStream objectInputStream = new ObjectInputStream((fileInputStream));
            return (User) objectInputStream.readObject();
        } catch (InvalidClassException e) {
            // Users should not see this...
            System.out.println("Classes have changed, try clearing the files.");
            return null;
        } catch (FileNotFoundException e) {
            System.out.println("Uh oh, no file found for this user " + userName);
            return null;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            System.out.println("Something else has gone wrong loading " + userName);
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
     *
     * @return An ArrayList of all the user's routines.
     */
    ArrayList<Routine> getMyRoutines() {
        return myRoutines;
    }

    void setMyRoutines(ArrayList<Routine> myRoutines) {
        this.myRoutines = myRoutines;
    }

    void addRoutine(Routine routine) {
        myRoutines.add(routine);
    }

    /**
     * Gets all the reports for this user.
     *
     * @return An ArrayList of all the user's reports.
     */
    ArrayList<Report> getMyReports() {
        return myReports;
    }

    synchronized void addReport(Report report) {
        myReports.add(report);
    }

    int getID() {
        return id;
    }

    void setID(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getUserName() {
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
    synchronized void saveUserDataFile() {
        final String USER_FILE = FILE_DIR + "USER_" + this.getUserName();
        try (FileOutputStream fileOutputStream = new FileOutputStream(USER_FILE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
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
