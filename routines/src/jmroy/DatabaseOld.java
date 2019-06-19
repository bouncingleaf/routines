//package jmroy;
//
///* Class Database (originally based on SimpleApp.java from Apache's Derby demo)
// *
// * This program will will try to connect to a network server on this
// * host (the localhost). Creates a database by making a connection to
// * Derby (automatically loading the driver).
// */
//
//import java.sql.*;
//
//import java.util.ArrayList;
//import java.util.Properties;
//
//
///**
// * Provides database operations specific to the Routines program
// */
//class Database {
//
//    /**
//     * This enum contains all the definitions for all of the tables.
//     * * name is a String with the name of the table
//     * * definition is a String with the list of columns, for createTable
//     * <p>
//     * *** LIST TABLES HERE IN CREATION ORDER ***
//     * That is:
//     * 1. First, tables that don't have any dependencies
//     * on other tables (i.e., no foreign keys),
//     * 2. Next, tables that depend ONLY on tables created in
//     * the first round.
//     * 3. In the third round, tables that depend ONLY on
//     * tables created in the first or second rounds,
//     * ...and so on.
//     * </p>
//     */
//    enum Table {
//        // Round One: Tables with no foreign keys
//        THEME("Theme",
//                "theme_name varchar(20) not null, " +
//                        "theme_file varchar(30) not null, " +
//                        "theme_show boolean, " +
//                        "primary key (theme_name)"),
//        // Round Two: Tables with only foreign keys from round one
//        USER("UserTable",
//                "user_id int not null, " +
//                        "username varchar(30), " +
//                        "name varchar(50), " +
//                        "theme varchar(20), " +
//                        "primary key (user_id), " +
//                        "constraint fk_user_theme foreign key (theme) " +
//                        "references Theme (theme_name)"),
//        // Round Three: Tables with only foreign keys from 1 and 2;
//        ROUTINE("Routine",
//                "routine_id int not null, " +
//                        "routine_name varchar(255)," +
//                        "routine_user_id int, " +
//                        "primary key (routine_id), " +
//                        "constraint fk_routine_user foreign key (routine_user_id) " +
//                        "references UserTable (user_id)"),
//        // Round Four: Tables with only foreign keys from rounds 1-3
//        TASK("Task",
//                "task_id int not null, " +
//                        "task_name varchar(255), " +
//                        "task_routine_id int, " +
//                        "task_active boolean, " +
//                        "primary key (task_id), " +
//                        "constraint fk_task_routine foreign key (task_routine_id) " +
//                        "references Routine (routine_id)"),
//        // Round Five...
//        TIMED_TASK("TimedTask",
//                "task_id int, " +
//                        "task_minutes int, " +
//                        "primary key (task_id), " +
//                        "constraint fk_timed_task_task foreign key (task_id) " +
//                        "references Task (task_id)"),
//        UNTIMED_TASK("UntimedTask",
//                "task_id int, " +
//                        "primary key (task_id), " +
//                        "constraint fk_untimed_task_task foreign key (task_id) " +
//                        "references Task (task_id)");
//
//        private final String name;
//        private final String definition;
//
//        Table(String name, String definition) {
//            this.name = name;
//            this.definition = definition;
//        }
//
//        String getTableName() {
//            return name;
//        }
//
//        String getTableDefinition() {
//            return definition;
//        }
//    }
//
//    private static Database currentDatabase;
//
//    static Database getDb() {
//        return currentDatabase;
//    }
//
//    static void createDb(boolean test) {
//        currentDatabase = new Database(test);
//    }
//
//    /* Statement and PreparedStatement objects, Connections, and ResultSets
//     * are resources that should be released explicitly after use.
//     */
//    private Connection conn = null;
//    private ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
//    private Statement s;
//
//    /**
//     * The initialization of the database. Should only need to do this once.
//     * Creates all the tables for the test database and the "live" database.
//     * If it doesn't work, make sure any old routinesTestDB and routinesDB
//     * files in the Derby bin directory are removed.
//     *
//     * @param args Arguments, ignored
//     */
//    public static void main(String[] args) {
//        // Should only need to do this once, unless databases are deleted
//        Database db = new Database(true);
//        db.createTables();
//        db = new Database(false);
//        db.createTables();
//        System.out.println("Done.");
//    }
//
//    /**
//     * Establishes a connection to the database
//     *
//     * @param test False if we're working with the "real" database
//     *             True if we're working with the test database
//     */
//    private Database(boolean test) {
//        String DB_NAME = test ? "routinesTestDB" : "routinesDB";
//        String PROTOCOL = "jdbc:derby://localhost:1527/";
//
//        try {
//            Properties props = new Properties(); // connection properties
//            // providing a user name and password is optional in the embedded
//            // and derbyclient frameworks
//            props.put("user", "user1");
//            props.put("password", "user1");
//
//            /* The schema name is the same as the user name (in this case
//             * "user1" or USER1.)
//             * Including create=true in the connection URL causes the database to
//             * be created when connecting for the first time.
//             */
//            conn = DriverManager.getConnection(PROTOCOL + DB_NAME
//                    + ";create=true", props);
//
//            System.out.println("Connected to database " + DB_NAME);
//
//            // We want to control transactions manually. Autocommit is on by default.
//            conn.setAutoCommit(false);
//
//            /* Creating a statement object that we can use for running various
//             * SQL statements commands against the database.*/
//            s = conn.createStatement();
//            statements.add(s);
//
//            commit("created db: " + DB_NAME);
//
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /*----------------------------------------------------------------------*
//     * General database methods
//     *----------------------------------------------------------------------*/
//
//    /**
//     * Creates all of the tables, in the specified order.
//     * Populates the themes table, if everything just got dropped
//     * Postcondition: Tables are created
//     */
//    private void createTables() {
//        try {
//            // Create the tables:
//            for (Table table : Table.values()) {
//                s.execute("create table " + table.getTableName() +
//                        "(" + table.getTableDefinition() + ")");
//            }
//            s.execute("CREATE SEQUENCE id_value AS BIGINT " +
//                    " START WITH 0 INCREMENT BY 1");
//            commit("created tables");
//            insertDefaults();
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    private void insertDefaults() {
//        try {
//            for (Theme theme : Theme.ALL_THEMES) {
//                insertTheme(theme);
//            }
//            // This is a bit of a hack, but it makes sure we always have at least
//            // one user, one routine, and one task.
//            User adminUser = new User(0, "admin", "Admin User", Theme.DEFAULT);
//            User.setSignedInUser(adminUser);
//            insertUser(adminUser, false);
//            insertRoutine(new Routine(0, "Admin Routine", 0), false);
//            insertTask(new UntimedTask(0, "Admin Task", 0, true), false);
//            commit("inserted defaults");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//
//    }
//
//    /**
//     * Cleans up if there's a crash
//     */
//    private void cleanUp() {
//        // release all open resources to avoid unnecessary memory usage
//        System.out.println("Cleanup is happening...");
//
//        // Statements and PreparedStatements
//        int i = 0;
//        while (!statements.isEmpty()) {
//            // Close all of the statements.
//            Statement st = statements.remove(i);
//            try {
//                if (st != null) {
//                    st.close();
//                    // This comment disables the warning in IntelliJ for the null assignment
//                    //noinspection UnusedAssignment
//                    st = null;
//                }
//                commit("st.close");
//            } catch (SQLException sqle) {
//                printSQLException(sqle);
//            }
//        }
//
//        //Connection
//        try {
//            if (conn != null) {
//                conn.close();
//                conn = null;
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//        }
//
//    }
//
//    /**
//     * Commits the transaction, logging a message
//     *
//     * @throws SQLException if commit fails
//     */
//    private void commit(String message) throws SQLException {
//        conn.commit();
//        System.out.println("Committed: " + message);
//    }
//
//    /**
//     * Prints details of an SQLException chain to <code>System.err</code>.
//     * Details included are SQL State, Error code, Exception message.
//     *
//     * @param e the SQLException from which to print details.
//     */
//    private static void printSQLException(SQLException e) {
//        // Unwraps the entire exception chain to unveil the real cause of the
//        // Exception.
//        while (e != null) {
//            System.err.println("\n----- SQLException -----");
//            System.err.println("  SQL State:  " + e.getSQLState());
//            System.err.println("  Error Code: " + e.getErrorCode());
//            System.err.println("  Message:    " + e.getMessage());
//            // for stack traces, refer to derby.log or uncomment this:
//            //e.printStackTrace(System.err);
//            e = e.getNextException();
//        }
//    }
//
//    /*----------------------------------------------------------------------*
//     * User table methods
//     *----------------------------------------------------------------------*/
//
//    /**
//     * Insert a user if it's a new user; update if it's an existing user
//     * And save the user's routines, and the routines' tasks
//     *
//     * @param user The user to insert or update
//     */
//    void saveUser(User user) {
//        if (getUserByUsername(user.getUserName()) == null) {
//            // New user, not found in DB
//            insertUser(user, true);
//            for (Routine routine : user.getMyRoutines()) {
//                insertRoutine(routine, true);
//                for (Task task : routine.getTasks()) {
//                    insertTask(task, true);
//                }
//            }
//        } else {
//            // Users can't delete a routine or task
//            // So all routines and tasks are either new or updated
//            updateUser(user, true);
//            for (Routine routine : user.getMyRoutines()) {
//                upsertRoutine(routine, true);
//                // Users can't delete a task from a routine
//                for (Task task : routine.getTasks()) {
//                    upsertTask(task, true);
//                }
//            }
//        }
//    }
//
//    /**
//     * Insert a User if it's a new User; update if it's an existing User
//     *
//     * @param user The User to insert or update
//     */
//    void upsertUserAndCommit(User user) {
//        if (getUserByID(user.getID()) == null) {
//            insertUser(user, true);
//        } else {
//            updateUser(user, true);
//        }
//    }
//
//    /**
//     * Insert a new user
//     *
//     * @param user              The user to insert
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    void insertUser(User user, boolean commitTransaction) {
//        PreparedStatement psInsert;
//
//        try {
//            // parameter 1 is username (varchar),
//            // parameter 2 is name (varchar),
//            // parameter 3 is theme (varchar)
//            psInsert = conn.prepareStatement("insert into " +
//                    Table.USER.getTableName() +
//                    " (user_id, username, name, theme) values (?, ?, ?, ?)");
//            statements.add(psInsert);
//            psInsert.setInt(1, user.getID());
//            psInsert.setString(2, user.getUserName());
//            psInsert.setString(3, user.getName());
//            psInsert.setString(4, user.getThemePreference().getName());
//            psInsert.executeUpdate();
//            if (commitTransaction) {
//                commit("insert user " + user.getName());
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Update an existing user
//     *
//     * @param user              The user to update
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    void updateUser(User user, @SuppressWarnings("SameParameterValue") boolean commitTransaction) {
//        PreparedStatement psUpdate;
//        try {
//            psUpdate = conn.prepareStatement(
//                    "update " + Table.USER.getTableName() + " set " +
//                            "username=?, " +
//                            "name=?, " +
//                            "theme=? " +
//                            "where user_id=?");
//            statements.add(psUpdate);
//            psUpdate.setString(1, user.getUserName());
//            psUpdate.setString(2, user.getName());
//            psUpdate.setString(3, user.getThemePreference().getName());
//            psUpdate.setInt(4, user.getID());
//            psUpdate.executeUpdate();
//            if (commitTransaction) {
//                commit("update user's name for " + user.getID() + " to " + user.getName());
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Get a list of all users
//     *
//     * @return An ArrayList of all users
//     */
//    ArrayList<User> queryUsers() {
//        ArrayList<User> users = new ArrayList<>();
//        ResultSet rs;
//        try {
//            rs = s.executeQuery("SELECT user_id, username, name, theme, theme_file, theme_show " +
//                    " FROM " + Table.USER.getTableName() +
//                    " LEFT JOIN " + Table.THEME.getTableName() +
//                    " ON " + Table.USER.getTableName() + ".theme = " +
//                    Table.THEME.getTableName() + ".theme_name" +
//                    " ORDER BY user_id");
//            while (rs.next()) {
//                users.add(new User(
//                        rs.getInt(1),
//                        rs.getString(2),
//                        rs.getString(3),
//                        new Theme(
//                                rs.getString(4),
//                                rs.getString(5),
//                                rs.getBoolean(6)
//                        )
//                ));
//            }
//            commit("queryUsers");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return users;
//    }
//
//    /**
//     * Max user_id from UserTable
//     *
//     * @return An int, the largest user_id
//     */
//    int getMaxUserID() {
//        ResultSet rs;
//        int max = 0;
//        try {
//            rs = s.executeQuery("SELECT MAX (user_id) " +
//                    " FROM " + Table.USER.getTableName());
//            if (rs.next()) {
//                max = rs.getInt(1);
//            }
//            commit("getMaxUserID");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        System.out.println("max user is " + max);
//        return max;
//    }
//
//    /**
//     * Get a user based on their username
//     *
//     * @param usernameInput The username of the user to get
//     * @return The User corresponding to that username
//     */
//    User getUserByUsername(String usernameInput) {
//        PreparedStatement psQuery;
//        User user = null;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT user_id, username, name, theme, theme_file, theme_show " +
//                            "FROM " + Table.USER.getTableName() +
//                            " LEFT JOIN " + Table.THEME.getTableName() +
//                            " ON " + Table.USER.getTableName() + ".theme = " +
//                            Table.THEME.getTableName() + ".theme_name" +
//                            " WHERE username=?"
//            );
//            statements.add(psQuery);
//            psQuery.setString(1, usernameInput);
//            rs = psQuery.executeQuery();
//            if (rs.next()) {
//                int user_id = rs.getInt(1);
//                String username = rs.getString(2);
//                String name = rs.getString(3);
//                Theme theme = new Theme(
//                        rs.getString(4),
//                        rs.getString(5),
//                        rs.getBoolean(6)
//                );
//                user = new User(user_id, username, name, theme);
//            }
//            commit("getUserByUsername " + usernameInput);
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return user;
//    }
//
//    /**
//     * Get a user based on their ID
//     *
//     * @param idInput The ID of the user to get
//     * @return The User corresponding to that ID
//     */
//    private User getUserByID(int idInput) {
//        System.out.println("input is " + idInput);
//        PreparedStatement psQuery;
//        User user = null;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT user_id, username, name, theme, theme_file, theme_show " +
//                            "FROM " + Table.USER.getTableName() +
//                            " LEFT JOIN " + Table.THEME.getTableName() +
//                            " ON " + Table.USER.getTableName() + ".theme = " +
//                            Table.THEME.getTableName() + ".theme_name" +
//                            " WHERE user_id=?"
//            );
//            statements.add(psQuery);
//            psQuery.setInt(1, idInput);
//            rs = psQuery.executeQuery();
//            if (rs.next()) {
//                user = new User(
//                        rs.getInt(1),
//                        rs.getString(2),
//                        rs.getString(3),
//                        new Theme(
//                                rs.getString(4),
//                                rs.getString(5),
//                                rs.getBoolean(6)
//                        )
//                );
//            }
//            commit("getUserByID");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return user;
//    }
//
//    /*----------------------------------------------------------------------*
//     * Theme table methods
//     *----------------------------------------------------------------------*/
//
//    /**
//     * Insert a new theme into the database.
//     * For now, themes are only built by the application.
//     *
//     * @param theme The Theme to insert
//     */
//    private void insertTheme(Theme theme) {
//        PreparedStatement psInsert;
//
//        try {
//            // parameter 1 is theme_name (varchar),
//            // parameter 2 is theme_file (varchar),
//            // parameter 3 is theme_show (boolean)
//            psInsert = conn.prepareStatement(
//                    "insert into " + Table.THEME.getTableName() + " values (?, ?, ?)");
//            statements.add(psInsert);
//            psInsert.setString(1, theme.getName());
//            psInsert.setString(2, theme.getFilename());
//            psInsert.setBoolean(3, theme.getShowTheme());
//            psInsert.executeUpdate();
//            commit("insert theme: " + theme.getName());
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Get a Theme from the database by name
//     *
//     * @param name The name of the theme to get
//     * @return The Theme requested, or null if not found
//     */
//    Theme getTheme(String name) {
//        ResultSet rs;
//        Theme themeFound = null;
//        try {
//            PreparedStatement psSearch = conn.prepareStatement(
//                    "SELECT theme_name, theme_file, theme_show" +
//                            " FROM " + Table.THEME.getTableName() +
//                            " WHERE theme_name=?"
//            );
//            statements.add(psSearch);
//            psSearch.setString(1, name);
//            rs = psSearch.executeQuery();
//
//            if (rs.next()) {
//                themeFound = new Theme(
//                        rs.getString(1),
//                        rs.getString(2),
//                        rs.getBoolean(3));
//            }
//
//            commit("query theme: " + name);
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return themeFound;
//    }
//
//    /**
//     * Get all themes from the database
//     *
//     * @return An ArrayList of themes
//     */
//    ArrayList<Theme> queryThemes() {
//        ArrayList<Theme> themes = new ArrayList<>();
//        ResultSet rs;
//        try {
//            rs = s.executeQuery("SELECT theme_name, theme_file, theme_show" +
//                    " FROM " + Table.THEME.getTableName() +
//                    " ORDER BY theme_name");
//            while (rs.next()) {
//                themes.add(new Theme(
//                        rs.getString(1),
//                        rs.getString(2),
//                        rs.getBoolean(3)
//                ));
//            }
//
//            commit("queryThemes");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return themes;
//    }
//
//    /*----------------------------------------------------------------------*
//     * Routine table methods
//     *----------------------------------------------------------------------*/
//
//    /**
//     * Insert a routine if it's a new routine; update if it's an existing routine
//     *
//     * @param routine           The routine to insert or update
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void upsertRoutine(Routine routine, @SuppressWarnings("SameParameterValue") boolean commitTransaction) {
//        if (getRoutineByID(routine.getID()) == null) {
//            insertRoutine(routine, commitTransaction);
//        } else {
//            updateRoutine(routine, commitTransaction);
//        }
//    }
//
//    /**
//     * Insert a new routine
//     *
//     * @param routine           The routine to insert
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void insertRoutine(Routine routine, boolean commitTransaction) {
//        PreparedStatement psInsert;
//
//        try {
//            psInsert = conn.prepareStatement("insert into " +
//                    Table.ROUTINE.getTableName() +
//                    " (routine_id, routine_name, routine_user_id) values (?, ?, ?)");
//            statements.add(psInsert);
//            // parameter 1 is routine_name (varchar),
//            // parameter 2 is routine_user_id (int),
//            psInsert.setInt(1, routine.getID());
//            psInsert.setString(2, routine.getName());
//            psInsert.setInt(3, routine.getUserID());
//            psInsert.executeUpdate();
//            if (commitTransaction) {
//                commit("insertRoutine");
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Update an existing routine
//     *
//     * @param routine           The routine to update
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void updateRoutine(Routine routine, boolean commitTransaction) {
//        PreparedStatement psUpdate;
//        try {
//            psUpdate = conn.prepareStatement(
//                    "update " + Table.ROUTINE.getTableName() + " set " +
//                            "routine_name=?, " +
//                            "routine_user_id=?, " +
//                            "where routine_id=?");
//            statements.add(psUpdate);
//            // parameter 1 is routine_name (varchar),
//            // parameter 2 is routine_user_id (int),
//            // parameter 3 is routine_id (int)
//            psUpdate.setString(1, routine.getName());
//            psUpdate.setInt(2, User.getSignedInUser().getID());
//            psUpdate.setInt(3, routine.getID());
//            psUpdate.executeUpdate();
//            if (commitTransaction) {
//                commit("updateRoutine");
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Get a list of all routines
//     *
//     * @return An ArrayList of all routines
//     */
//    ArrayList<Routine> queryRoutinesForUser(User user) {
//        ArrayList<Routine> routines = new ArrayList<>();
//        PreparedStatement psQuery;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT routine_id, routine_name, routine_user_id " +
//                            "FROM " + Table.ROUTINE.getTableName() +
//                            " WHERE routine_user_id=? " +
//                            " ORDER BY routine_id"
//            );
//            statements.add(psQuery);
//            // Parameter is routine_user_id (int)
//            psQuery.setInt(1, user.getID());
//            rs = psQuery.executeQuery();
//            while (rs.next()) {
//                routines.add(new Routine(
//                        rs.getInt(1),
//                        rs.getString(2),
//                        rs.getInt(3)
//                ));
//            }
//            commit("queryRoutinesForUser");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return routines;
//    }
//
//    /**
//     * Get a routine based on their id
//     *
//     * @param idInput The id of the routine to get
//     * @return The Routine corresponding to that id
//     */
//    private Routine getRoutineByID(int idInput) {
//        PreparedStatement psQuery;
//        Routine routine = null;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT routine_id, routine_name, routine_user_id " +
//                            "FROM " + Table.ROUTINE.getTableName() +
//                            " WHERE routine_id=?"
//            );
//            statements.add(psQuery);
//            psQuery.setInt(1, idInput);
//            rs = psQuery.executeQuery();
//            if (rs.next()) {
//                int routine_id = rs.getInt(1);
//                String routine_name = rs.getString(2);
//                routine = new Routine(routine_id, routine_name, rs.getInt(3));
//            }
//            commit("getRoutineByID " + idInput);
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return routine;
//    }
//
//    /**
//     * Max routine_id from Routine table
//     *
//     * @return An int, the largest routine_id
//     */
//    int getMaxRoutineID() {
//        ResultSet rs;
//        int max = 0;
//        try {
//            rs = s.executeQuery("SELECT MAX (routine_id) " +
//                    " FROM " + Table.ROUTINE.getTableName());
//            if (rs.next()) {
//                max = rs.getInt(1);
//            }
//            commit("getMaxRoutineID");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        System.out.println("max routine is " + max);
//        return max;
//    }
//
//    /*----------------------------------------------------------------------*
//     * Task (and TimedTask, and UntimedTask) table methods
//     *----------------------------------------------------------------------*/
//
//    /**
//     * Insert a task if it's a new task; update if it's an existing task
//     *
//     * @param task              The Task to insert or update
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void upsertTask(Task task, @SuppressWarnings("SameParameterValue") boolean commitTransaction) {
//        if (getTaskByID(task.getID()) == null) {
//            insertTask(task, commitTransaction);
//        } else {
//            updateTask(task, commitTransaction);
//        }
//    }
//
//    /**
//     * Insert a new task
//     *
//     * @param task              The task to insert
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void insertTask(Task task, boolean commitTransaction) {
//        System.out.println(task.getID() + " " + task.getName() + " " + task.getRoutineID());
//        PreparedStatement psInsert;
//        PreparedStatement psInsertSubtype;
//
//        try {
//            // Insert into the Task table
//            psInsert = conn.prepareStatement("insert into " +
//                    Table.TASK.getTableName() +
//                    " (task_id, task_name, task_routine_id) values (?, ?, ?)");
//            statements.add(psInsert);
//            // parameter 1 is task_id (int)
//            // parameter 2 is task_name (varchar),
//            // parameter 3 is task_routine_id (int),
//            psInsert.setInt(1, task.getID());
//            psInsert.setString(2, task.getName());
//            psInsert.setInt(3, task.getRoutineID());
//            psInsert.executeUpdate();
//
//            // Insert into the TimedTask or UntimedTask table also
//            String tableInsert = task instanceof TimedTask ?
//                    Table.TIMED_TASK.getTableName() +
//                            " (task_id, task_minutes) values (?, ?, ?, ?)":
//                    Table.UNTIMED_TASK.getTableName() +
//                            " (task_id) values (?)";
//            psInsertSubtype = conn.prepareStatement("insert into " + tableInsert);
//            psInsertSubtype.setInt(1, task.getID());
//            if (task instanceof TimedTask) {
//                psInsertSubtype.setInt(2, ((TimedTask) task).getMinutes());
//            }
//            psInsertSubtype.executeUpdate();
//
//            if (commitTransaction) {
//                commit("insert task " + task.getName());
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Update an existing task
//     *
//     * @param task              The task to update
//     * @param commitTransaction True to commit, false to skip commit
//     *                          Useful if transaction is in progress
//     */
//    private void updateTask(Task task, boolean commitTransaction) {
//        PreparedStatement psUpdate;
//        try {
//            psUpdate = conn.prepareStatement(
//                    "update " + Table.TASK.getTableName() + " set " +
//                            "task_name=?, " +
//                            "task_user_id=?, " +
//                            "task_active=? " +
//                            "where task_id=?");
//            statements.add(psUpdate);
//            // parameter 1 is task_name (varchar),
//            // parameter 2 is task_user_id (int),
//            // parameter 3 is task_active (boolean),
//            // parameter 4 is task_id (int)
//            psUpdate.setString(1, task.getName());
//            psUpdate.setInt(2, User.getSignedInUser().getID());
//            psUpdate.setInt(3, task.getID());
//            psUpdate.executeUpdate();
//            if (commitTransaction) {
//                commit("update task for " + task.getID() + " to " + task.getName());
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//    }
//
//    /**
//     * Get a list of all routines
//     *
//     * @return An ArrayList of all routines
//     */
//    ArrayList<Task> queryTasksForRoutine(Routine routine) {
//        ArrayList<Task> tasks = new ArrayList<>();
//        PreparedStatement psQuery;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT Task.task_id, task_name, task_routine_id, task_minutes, task_active" +
//                            " FROM " + Table.TASK.getTableName() +
//                            " LEFT JOIN " + Table.TIMED_TASK.getTableName() +
//                            " ON Task.task_id = TimedTask.task_id" +
//                            " WHERE task_routine_id=? "
//            );
//            statements.add(psQuery);
//            psQuery.setInt(1, routine.getID());
//            rs = psQuery.executeQuery();
//            while (rs.next()) {
//                tasks.add(
//                        rs.getInt(4) > 0 ?
//                                new TimedTask(
//                                        rs.getInt(1),
//                                        rs.getString(2),
//                                        rs.getInt(3),
//                                        rs.getInt(4),
//                                        rs.getBoolean(5)
//                                ) :
//                                new UntimedTask(
//                                        rs.getInt(1),
//                                        rs.getString(2),
//                                        rs.getInt(3),
//                                        rs.getBoolean(5)
//                                ));
//            }
//            commit("queryTasksForRoutine");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return tasks;
//    }
//
//    /**
//     * Get a task from the database by Task id
//     *
//     * @param idInput The id of the task to get
//     * @return The Task corresponding to that id
//     */
//    private Task getTaskByID(int idInput) {
//        PreparedStatement psQuery;
//        Task task = null;
//        ResultSet rs;
//        try {
//            psQuery = conn.prepareStatement(
//                    "SELECT Task.task_id, task_name, task_routine_id, task_minutes, task_active " +
//                            "FROM " + Table.TASK.getTableName() +
//                            " LEFT JOIN " + Table.TIMED_TASK.getTableName() +
//                            " ON Task.task_id = TimedTask.task_id" +
//                            " WHERE Task.task_id=?"
//            );
//            statements.add(psQuery);
//            psQuery.setInt(1, idInput);
//            rs = psQuery.executeQuery();
//            if (rs.next()) {
//                int task_id = rs.getInt(1);
//                String task_name = rs.getString(2);
//                int task_routine_id = rs.getInt(3);
//                int task_minutes = rs.getInt(4);
//                boolean task_active = rs.getBoolean(5);
//                // TODO: Distinguish timed and untimed
//                task = task_minutes > 0 ?
//                        new TimedTask(task_id, task_name, task_routine_id, task_minutes, task_active) :
//                        new UntimedTask(task_id, task_name, task_routine_id, task_active);
//            }
//            commit("getRoutineByID");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return task;
//    }
//
//    /**
//     * Max task_id from Task table
//     *
//     * @return An int, the largest task_id
//     */
//    int getMaxTaskID() {
//        ResultSet rs;
//        int max = 0;
//        try {
//            rs = s.executeQuery("SELECT MAX (task_id) " +
//                    " FROM " + Table.TASK.getTableName());
//            if (rs.next()) {
//                max = rs.getInt(1);
//            }
//            commit("getMaxTaskID");
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        System.out.println("max task is " + max);
//        return max;
//    }
//
//
//}