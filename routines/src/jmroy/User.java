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
    private long id;
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
        // Poor id choice for a production system, but it should work for now
        this.id = System.currentTimeMillis();
        this.name = name.equals("") ? userName : name;
    }

    /**
     * Constructor for loading existing an existing user from the database
     * @param userName The user's chosen username
     * @param name The user's chosen name, or empty string to just use username
     * @param themeName The name of the user's chosen theme
     * @param id The user's already existing ID
     */
    User(long id, String userName, String name, String themeName) {
        this(userName);
        this.id = id;
        this.name = name.equals("") ? userName : name;
        this.themePreference = Theme.getThemeByName(themeName);
    }

    // Class Methods

    private static void setSignedInUser(User user) {
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
        getSignedInUser().save();
    }

    static User load (String username) {
        return Database.getDb().getUserByUsername(username);
    }

    static String getStylesheet() {
        return getSignedInUser() == null ? Theme.DEFAULT.getFilename() :
                getSignedInUser().getThemePreference().getFilename();
    }

    // Instance Methods

    long getId () { return id; }

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

    void save() {
        Database.getDb().upsertUser(this);
    }

    Theme getThemePreference() {
        return themePreference;
    }

    void setThemePreference(Theme themePreference) {
        this.themePreference = themePreference;
    }

}
