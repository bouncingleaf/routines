package jmroy;

import java.util.ArrayList;

/**
 * User class - manages the user's info including their routines
 * Also responsible for loading/saving user data to storage.
 * @author Jessica Roy
 */
class User {

    // Class variables and constants
    private static User signedInUser;
    // Usernames may not begin with this string - reserved for unit testing
    static final String TEST_USER = "junittest";

    // Instance variables
    private int id;
    private ArrayList<Routine> myRoutines;
    private String name;
    private String userName;
    private Theme themePreference = Theme.DEFAULT;

    /**
     * Base constructor to assign name and new routines list
     * @param userName The user's chosen username
     */
    private User(String userName)
    {
        // This is a terrible way to assign a unique ID but it will do for now
        this.id = getNewUserID();
        this.myRoutines = new ArrayList<>();
        this.userName = userName;
    }

    /**
     * Constructor for building a new user with the default theme
     * @param userName The user's chosen username
     * @param name The user's chosen name, or empty string to just use username
     */
    User(String userName, String name) {
        this(userName);
        this.name = name.equals("") ? userName : name;
    }

    /**
     * Constructor for loading existing an existing user from the database
     * @param id The user's already existing ID
     * @param userName The user's chosen username
     * @param name The user's chosen name, or empty string to just use username
     * @param theme The user's chosen theme
     */
    User(int id, String userName, String name, Theme theme) {
        this.myRoutines = new ArrayList<>();
        this.id = id;
        this.userName = userName;
        this.name = name.equals("") ? userName : name;
        this.themePreference = theme;
    }

    // Class Methods

    private static int getNewUserID() {
        return Database.getDb().getMaxUserID() + 1;
    }

    static void setSignedInUser(User user) {
        signedInUser = user;
    }

    static User getSignedInUser() {
        return signedInUser;
    }

    static String userNamePurify(String name){
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    static Screen.Pages signIn(String nameInput) {
        String username = userNamePurify(nameInput);
        if (username.length() > 0) {
            User user = User.load(username);
            if (user != null) {
                setSignedInUser(user);
                return Screen.Pages.MAIN;
            } else {
                setSignedInUser(new User(username));
                return Screen.Pages.NAME;
            }
        } else {
            System.out.println("Sign in not valid");
            return Screen.Pages.ERROR;
        }
    }

    static void signUp(String nameInput) {
        String name = nameInput.replaceAll("[^a-zA-Z0-9\']"," ");
        getSignedInUser().setName( name.length() > 0 ? name : User.getSignedInUser().getUserName());
    }

    static User load(String username) {
        User user = Database.getDb().getUserByUsername(username);
        if (user != null) {
            user.setMyRoutines(user.loadRoutines());
        }
        return user;
    }

    private ArrayList<Routine> loadRoutines() {
        ArrayList<Routine> routines = Database.getDb().queryRoutinesForUser(this);
        for(Routine routine : routines) {
            routine.loadTasks();
        }
        return routines;
    }

    static String getStylesheet() {
        return getSignedInUser() == null ? Theme.DEFAULT.getFilename() :
                getSignedInUser().getThemePreference().getFilename();
    }

    // Instance Methods

    int getID () { return id; }

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
     * The caller must call save() as needed.
     */
    void deleteRoutines() {
        this.myRoutines = new ArrayList<>();
    }

    /**
     * Saves the user, the user's routines, and the user's routines' tasks
     * to the database
     */
    void save() {
        // TODO: This does not handle deletes properly! Fix
        Database.getDb().saveUser(this);
    }

    Theme getThemePreference() {
        return themePreference;
    }

    void setThemePreference(Theme themePreference) {
        this.themePreference = themePreference;
    }

}
