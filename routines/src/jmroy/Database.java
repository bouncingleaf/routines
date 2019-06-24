package jmroy;

/* Class Database (originally based on SimpleApp.java from Apache's Derby demo)
 *
 * This program will will try to connect to a network server on this
 * host (the localhost). Creates a database by making a connection to
 * Derby (automatically loading the driver).
 */

import java.sql.*;

import java.util.ArrayList;
import java.util.Properties;


/**
 * Provides database operations specific to the Routines program
 */
class Database {

    /**
     * name is a String with the name of the table
     * definition is a String with the list of columns, for createTable
     * <p>
     * *** LIST TABLES HERE IN CREATION ORDER ***
     * That is:
     * 1. First, tables that don't have any dependencies
     * on other tables (i.e., no foreign keys),
     * 2. Next, tables that depend ONLY on tables created in
     * the first round.
     * 3. In the third round, tables that depend ONLY on
     * tables created in the first or second rounds,
     * ...and so on.
     * </p>
     */
    enum Table {
        // Round One: Tables with no foreign keys
        THEME("Theme",
                "theme_name varchar(20) not null, " +
                        "theme_file varchar(30) not null, " +
                        "primary key (theme_name)"),
        // Round Two: Tables with only foreign keys from round one
        USER("UserTable",
                "user_id int not null generated always as identity, " +
                        "username varchar(50) unique, " +
                        "name varchar(50), " +
                        "theme_name varchar(20), " +
                        "primary key (user_id), " +
                        "constraint fk_user_theme foreign key (theme_name) references Theme (theme_name)");

        private final String name;
        private final String definition;

        Table(String name, String definition) {
            this.name = name;
            this.definition = definition;
        }

        String getTableName() {
            return name;
        }
        String getTableDefinition() {
            return definition;
        }
    }

    private static Database currentDatabase;

    static Database getDb() {
        return currentDatabase;
    }

    static void createDb(boolean test) {
        currentDatabase = new Database(test);
    }

    /* We will be using Statement and PreparedStatement objects for
     * executing SQL. These objects, as well as Connections and ResultSets,
     * are resources that should be released explicitly after use, hence
     * the try-catch-finally pattern used below.
     */
    private Connection conn = null;
    private ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
    private Statement s;

    /*
     * This program will will try to connect to a network server on this
     * host (the localhost).
     * Creates a database by making a connection to Derby (automatically loading
     * the driver)
     */

    /**
     * The initialization of the database. Should only need to do this once.
     * Creates all the tables for the test database and the "live" database.
     *
     * @param args Arguments, ignored
     */
    public static void main(String[] args) {
        Database db = new Database(true);
        db.createTables();
        db = new Database(false);
        db.createTables();
        System.out.println("Done building database.");
    }

    private Database(boolean test) {
        String DB_NAME = test ? "routinesTestDB" : "routinesDB";
        String PROTOCOL = "jdbc:derby://localhost:1527/";

        try {
            Properties props = new Properties(); // connection properties
            // providing a user name and password is optional in the embedded
            // and derbyclient frameworks
            props.put("user", "user1");
            props.put("password", "user1");

            /* The schema name is the same as the user name (in this case
             * "user1" or USER1.)
             * Including create=true in the connection URL causes the database to
             * be created when connecting for the first time.
             */
            conn = DriverManager.getConnection(PROTOCOL + DB_NAME
                    + ";create=true", props);

            System.out.println("Connected to database " + DB_NAME);

            // We want to control transactions manually. Autocommit is on by default.
            conn.setAutoCommit(false);

            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            s = conn.createStatement();
            statements.add(s);

            commit("created db: " + DB_NAME);

        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    /*----------------------------------------------------------------------*
     * General database methods
     *----------------------------------------------------------------------*/

    /**
     * Creates all of the tables, in the specified order.
     * Populates the themes table, if everything just got dropped
     * Postcondition: Tables are created
     */
    private void createTables() {
        try {
            // Create the tables:
            for (Table table : Table.values()) {
                s.execute("create table " + table.getTableName() +
                        "(" + table.getTableDefinition() + ")");
            }
            commit("created tables");
            for (Theme theme : Theme.ALL_THEMES) {
                insertTheme(theme);
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    private void cleanUp() {
        // release all open resources to avoid unnecessary memory usage
        System.out.println("Cleanup is happening...");

        // Statements and PreparedStatements
        int i = 0;
        while (!statements.isEmpty()) {
            // Close all of the statements.
            Statement st = statements.remove(i);
            try {
                if (st != null) {
                    st.close();
                    // This comment disables the warning in IntelliJ for the null assignment
                    //noinspection UnusedAssignment
                    st = null;
                }
                commit("st.close");
            } catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }

        //Connection
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }

    }

    /**
     * @throws SQLException if commit fails
     */
    private void commit(String message) throws SQLException {
        conn.commit();
        System.out.println("Committed: " + message);
    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    private static void printSQLException(SQLException e) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }

    /*----------------------------------------------------------------------*
     * User table methods
     *----------------------------------------------------------------------*/

    /**
     * If the user is new, inserts the user; otherwise, updates the user
     * @param user The User to insert or update
     */
    void upsertUser(User user) {
        if (getUserByUsername(user.getUserName()) == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
    }

    /**
     * Inserts the user into the database
     * @param user The User to insert
     */
    void insertUser(User user) {
        PreparedStatement psInsert;

        try {
            // parameter 1 is username (varchar),
            // parameter 2 is name (varchar),
            // parameter 3 is theme (varchar)
            psInsert = conn.prepareStatement("insert into " +
                    Table.USER.getTableName() +
                    " (username, name, theme_name) values (?, ?, ?)");
            statements.add(psInsert);
            psInsert.setString(1, user.getUserName());
            psInsert.setString(2, user.getName());
            psInsert.setString(3, user.getThemePreference().getName());
            psInsert.executeUpdate();
            commit("insert user " + user.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    /**
     * Updates a user in the database
     * @param user The User to update
     */
    void updateUser(User user) {
        PreparedStatement psUpdate;
        try {
            psUpdate = conn.prepareStatement(
                    "update " + Table.USER.getTableName() + " set " +
                            "username=?, " +
                            "name=?, " +
                            "theme_name=? " +
                            "where user_id=?");
            statements.add(psUpdate);
            String newName = user.getName();
            System.out.println("new name is " + newName);
            psUpdate.setString(1, user.getUserName());
            psUpdate.setString(2, newName);
            psUpdate.setString(3, user.getThemePreference().getName());
            psUpdate.setInt(4, user.getID());
            psUpdate.executeUpdate();
            commit("update user's name for " + user.getID() + " to " + user.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    ArrayList<User> queryUsers() {
        ArrayList<User> users = new ArrayList<>();
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT user_id, username, name, UserTable.theme_name, theme_file " +
                    " FROM " + Table.USER.getTableName() +
                    " JOIN " + Table.THEME.getTableName() +
                    " ON " + Table.USER.getTableName() + ".theme_name = " +
                    Table.THEME.getTableName() + ".theme_name" +
                    " ORDER BY user_id");
            while (rs.next()) {
                users.add(new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        new Theme(
                                rs.getString(4),
                                rs.getString(5)
                        )
                ));
            }
            commit("queryUsers");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return users;
    }

    /**
     * Given the username, returns the user ID
     * @param usernameInput The username to search for
     * @return An integer, the user ID
     */
    Integer getUserIDByUsername(String usernameInput) {
        PreparedStatement psQuery;
        Integer id = null;
        ResultSet rs;
        try {
            psQuery = conn.prepareStatement(
                    "SELECT user_id " +
                            "FROM " + Table.USER.getTableName() +
                            " WHERE username=?"
            );
            statements.add(psQuery);
            psQuery.setString(1, usernameInput);
            rs = psQuery.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            commit("getUser ID ByUsername");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return id;
    }

    /**
     * Determines if a given username is in the database or not.
     * @param usernameInput The username to look for
     * @return True if the user is found, false if not
     */
    boolean usernameInDatabase(String usernameInput) {
        PreparedStatement psQuery;
        boolean found = false;
        ResultSet rs;
        try {
            psQuery = conn.prepareStatement(
                    "SELECT user_id" +
                            " FROM " + Table.USER.getTableName() +
                            " WHERE username=?"
            );
            statements.add(psQuery);
            psQuery.setString(1, usernameInput);
            rs = psQuery.executeQuery();
            if (rs.next()) {
                found = true;
            }
            commit("username in db");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return found;
    }

    /**
     * Gets information about a user from the database, given a username
     * @param usernameInput The username to get data for
     * @return The User data
     */
    User getUserByUsername(String usernameInput) {
        PreparedStatement psQuery;
        User user = null;
        ResultSet rs;
        try {
            psQuery = conn.prepareStatement(
                    "SELECT user_id, username, name, UserTable.theme_name, theme_file" +
                            " FROM " + Table.USER.getTableName() +
                            " JOIN " + Table.THEME.getTableName() +
                            " ON " + Table.USER.getTableName() + ".theme_name = " +
                            Table.THEME.getTableName() + ".theme_name" +
                            " WHERE username=?"
            );
            statements.add(psQuery);
            psQuery.setString(1, usernameInput);
            rs = psQuery.executeQuery();
            if (rs.next()) {
                int user_id = rs.getInt(1);
                String username = rs.getString(2);
                String name = rs.getString(3);
                String theme_name = rs.getString(4);
                String theme_file = rs.getString(5);
                user = new User(user_id, username, name, new Theme (theme_name, theme_file));
                System.out.println(user.getName());
            }
            commit("getUserByUsername");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return user;
    }

    /*----------------------------------------------------------------------*
     * Theme table methods
     *----------------------------------------------------------------------*/

    /**
     * Inserts a theme into the database
     * @param theme The theme to insert
     */
    private void insertTheme(Theme theme) {
        PreparedStatement psInsert;

        try {
            // parameter 1 is name (varchar),
            // parameter 2 is filename (varchar),
            // parameter 3 is showTheme (boolean)
            psInsert = conn.prepareStatement(
                    "insert into " + Table.THEME.getTableName() + " values (?, ?)");
            statements.add(psInsert);
            psInsert.setString(1, theme.getName());
            psInsert.setString(2, theme.getFilename());
            psInsert.executeUpdate();
            commit("insert theme: " + theme.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

//    Theme getTheme(@SuppressWarnings("SameParameterValue") String name) {
//        ResultSet rs;
//        Theme themeFound = null;
//        try {
//            PreparedStatement psSearch = conn.prepareStatement(
//                    "SELECT theme_name, theme_file FROM " + Table.THEME.getTableName() +
//                            " WHERE theme_name=?"
//            );
//            statements.add(psSearch);
//            psSearch.setString(1, name);
//            rs = psSearch.executeQuery();
//
//            if (rs.next()) {
//                themeFound = new Theme(
//                        rs.getString(1),
//                        rs.getString(2));
//            }
//
//            commit("query theme: " + name);
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return themeFound;
//    }

    /**
     * Gets all the available themes
     * @return an ArrayList of Themes
     */
    ArrayList<Theme> queryThemes() {
        ArrayList<Theme> themes = new ArrayList<>();
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT theme_name, theme_file FROM " + Table.THEME.getTableName() + " ORDER BY theme_name");
            while (rs.next()) {
                themes.add(new Theme(
                        rs.getString(1),
                        rs.getString(2)
                ));
            }

            commit("queryThemes");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return themes;
    }

}