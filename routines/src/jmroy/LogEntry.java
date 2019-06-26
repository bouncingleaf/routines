package jmroy;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * The LogEntry class - defines a log entry to be used for reporting
 */
class LogEntry {
    private int id;
    private User user;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String routineName;

    /**
     * Constructor for a new log entry
     *
     * @param routineName The name of the routine
     */
    LogEntry(String routineName) {
        this.user = User.getSignedInUser();
        this.startDateTime = LocalDateTime.now();
        this.routineName = routineName;
    }

    /**
     * Constructor for existing log entries loaded from the database
     *
     * @param id            The id of the entry in the database
     * @param user          The User whose entry this is
     * @param startDateTime The time the routine was started
     * @param endDateTime   The time the routine was finished
     * @param routineName   The name of the routine
     */
    LogEntry(int id, User user, LocalDateTime startDateTime, LocalDateTime endDateTime, String routineName) {
        this.id = id;
        this.user = user;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.routineName = routineName;
    }

    int getID() {
        return id;
    }

    int getUserID() {
        return user.getID();
    }

    LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    String getRoutineName() {
        return routineName;
    }

    /**
     * Gets all the log entries for the current user
     *
     * @return An ArrayList of log entries
     */
    static ArrayList<LogEntry> getLogEntries() {
        return Database.getDb().queryLogEntriesForUser(User.getSignedInUser());
    }

    /**
     * Gets a specific log entry by its id
     *
     * @param id The id of the log entry to retrieve
     * @return The log entry corresponding with the given id
     */
    static LogEntry getLogEntryByID(int id) {
        return Database.getDb().getLogEntryByID(id);
    }

    /**
     * A simple way of displaying a log entry in a report.
     *
     * @return A string with the entry's dates and the name of the routine
     */
    @Override
    public String toString() {
        return (getStartDateTime() + " - " + getEndDateTime() + ": " + getRoutineName());
    }
}
