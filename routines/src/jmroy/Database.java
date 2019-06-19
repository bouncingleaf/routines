package jmroy;

/* Class Database (originally based on SimpleApp.java from Apache's Derby demo) */

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
                "name varchar(20) not null, " +
                        "filename varchar(30) not null," +
                        "showTheme boolean," +
                        "primary key (name)"),
        // Round Two: Tables with only foreign keys from round one
        USER("UserTable",
                "id int, " +
                        "name varchar(50), " +
                        "username varchar(30), " +
                        "theme varchar(20)," +
                        "primary key (id)," +
                        "foreign key (theme) references Theme (name)");

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

    private static Database db;


    static Database getDb() {
        return db;
    }

    static void createDb(boolean test) {
        db = new Database(test);
    }

    /* We will be using Statement and PreparedStatement objects for
     * executing SQL. These objects, as well as Connections and ResultSets,
     * are resources that should be released explicitly after use, hence
     * the try-catch-finally pattern used below.
     */
    private Connection conn = null;
    private ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
    private Statement s;

    /**
     * This program will will try to connect to a network server on this
     * host (the localhost).
     * Creates a database by making a connection to Derby (automatically loading
     * the driver)
     */

    private Database(boolean test) {
        String DB_NAME = test? "routinesTestDB" : "routinesDB";
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

            createTables();

        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    /*----------------------------------------------------------------------*
     * General database methods
     *----------------------------------------------------------------------*/

    /**
     * Drops tables first if this is the test DB
     * Creates all of the tables, in the specified order.
     * Populates the themes table, if everything just got dropped
     * Postcondition: Tables are created
     *
     */
    private void createTables() {
        try {
            // Create the tables:
            for (Table table : Table.values()) {
                s.execute("create table " + table.getTableName() +
                        "(" + table.getTableDefinition() + ")");
            }
            commit("created tables");
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

    void upsertUser(User user) {
        if (getUserByUsername(user.getUserName()) == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
    }

    void insertUser(User user) {
        PreparedStatement psInsert;

        try {
            // parameter 1 is id (int),
            // parameter 2 is name (varchar),
            // parameter 3 is username (varchar),
            // parameter 4 is theme (varchar)
            psInsert = conn.prepareStatement(
                    "insert into " + Table.USER.name() +
                            " values (?, ?, ?, ?)");
            statements.add(psInsert);

            psInsert.setLong(1, user.getId());
            psInsert.setString(2, user.getName());
            psInsert.setString(3, user.getUserName());
            psInsert.setString(4, user.getThemePreference().getName());
            psInsert.executeUpdate();
            commit("insert user" + user.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    void updateUser(User user) {
        PreparedStatement psUpdate;
        try {
            psUpdate = conn.prepareStatement(
                    "update " + Table.USER.name() +
                            "set name=? " +
                            "username=? " +
                            "theme=? " +
                            "where id=?");
            statements.add(psUpdate);
            psUpdate.setString(1, user.getName());
            psUpdate.setString(2, user.getUserName());
            psUpdate.setString(3, user.getThemePreference().getName());
            psUpdate.setLong(4, user.getId());
            psUpdate.executeUpdate();
            commit("update user's name for " + user.getId() + " to " + user.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    ArrayList<User> queryUsers() {
        ArrayList<User> users = new ArrayList<>();
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT id, name, username, theme " +
                    " FROM " + Table.USER.name() + " ORDER BY id");
            while (rs.next()) {
                users.add(new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                ));
            }
            commit("query");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return users;
    }

    User getUserByUsername(String username) {
        User user = null;
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT id, name, username, theme " +
                    "FROM " + Table.USER.name() + "WHERE username=" + username);
            if (rs.next()) {
                user = new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                );
            }
            commit("getUserByUsername");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return user;
    }

//    int getMaxUserId() {
//        ResultSet rs;
//        int max = 0;
//        try {
//            rs = s.executeQuery("SELECT max(id) FROM " + Table.USER.name());
//            commit("query");
//            if (rs.next()) {
//                max = rs.getInt(1);
//            }
//        } catch (SQLException sqle) {
//            printSQLException(sqle);
//            cleanUp();
//        }
//        return max;
//    }

    /*----------------------------------------------------------------------*
     * Theme table methods
     *----------------------------------------------------------------------*/

    void insertTheme(Theme theme) {
        PreparedStatement psInsert;

        try {
            // parameter 1 is name (varchar),
            // parameter 2 is filename (varchar),
            psInsert = conn.prepareStatement(
                    "insert into " + Table.THEME.name() + " values (?, ?, ?)");
            statements.add(psInsert);
            psInsert.setString(1, theme.getName());
            psInsert.setString(2, theme.getFilename());
            psInsert.setBoolean(3, theme.getShowTheme());
            psInsert.executeUpdate();
            commit("insert theme" + theme.getName());
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
    }

    Theme getTheme(String name) {
        ResultSet rs;
        Theme themeFound = null;
        try {
            PreparedStatement psSearch = conn.prepareStatement(
                    "SELECT name, filename FROM " + Table.THEME.name() +
                            "WHERE name=?"
            );
            statements.add(psSearch);
            psSearch.setString(1, name);
            rs = psSearch.executeQuery();

            if (rs.next()) {
                themeFound = new Theme(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getBoolean(3));
            }

            commit("query");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return themeFound;
    }

    ArrayList<Theme> queryThemes() {
        ArrayList<Theme> themes = new ArrayList<>();
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT name, filename FROM " + Table.THEME.name() + " ORDER BY name");
            while (rs.next()) {
                themes.add(new Theme(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getBoolean(3)
                ));
            }

            commit("query");
        } catch (SQLException sqle) {
            printSQLException(sqle);
            cleanUp();
        }
        return themes;
    }

}
