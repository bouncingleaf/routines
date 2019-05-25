package jmroy;

import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author Jessica Roy
 *
 */
class User implements Serializable {
    
    // Instance variables
    
    private ArrayList<Routine> myRoutines;
    private String name;
    private String userName;

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
     * Loads a User object by reading and deserializing it from a user data file.
     * @param fileDir The directory the user data file is stored in.
     * @param userName The userName of the User to load.
     * @return The User object loaded from the file.
     */
    static User load (String fileDir, String userName) {
        final String USER_FILE = fileDir + "USER_" + userName + ".txt";
        try (FileInputStream fileInputStream = new FileInputStream(USER_FILE)) {
            ObjectInputStream objectInputStream = new ObjectInputStream((fileInputStream));
            return (User) objectInputStream.readObject();
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Serializes the User object and saves the data to a file specific to that user.
     * @param fileDir The directory to store the user data file in.
     */
    void save (String fileDir) {
        final String USER_FILE = fileDir + "USER_" + this.getUserName() + ".txt";
        try (FileOutputStream fileOutputStream = new FileOutputStream(USER_FILE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (IOException e) {
            System.out.println("Problem saving user");
            e.printStackTrace();
        }

    }

}
