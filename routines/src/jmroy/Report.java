package jmroy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Report implements Serializable {
    private String title;
    private LocalDateTime runDateTime;

    /*
     * I was originally storing logEntries here, not logEntryIDs,
     * and populating the list of logEntries by way of a database search
     * for the user in question. However, I found that if I was storing
     * the LogEntry objects in an ArrayList on the Report... and the
     * Reports in an ArrayList on the User... this meant I was storing
     * all of the LogEntries in the User file! I'm already storing
     * them in the database, I don't want to store a second copy in the
     * user file. In a "real world" scenario, everything would be in the
     * database anyway and this wouldn't be an issue, but for the purposes
     * of this assignment, I switched to storing only the LogEntry IDs,
     * as a workaround.
     */
    private ArrayList<Integer> logEntryIDs;

    private Report(String title) {
        this.runDateTime = LocalDateTime.now();
        this.title = title;
    }

    /**
     * Creates a new report in a separate thread
     */
    static void createNewReport() {
        CreateReport task = new CreateReport("My report");
        System.out.println("Starting executor...");
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(task);
        executorService.shutdown();
        System.out.println("Task has been started.");
        Screen.goToScreen(Screen.Pages.MAIN);
    }

    static class CreateReport implements Runnable {
        private String newTitle;

        CreateReport(String newTitle) {
            this.newTitle = newTitle;
        }

        @Override
        public void run() {
            System.out.println("Thread starting to create report: " + newTitle);
            User user = User.getSignedInUser();
            // Simulate taking some time to make the report...
            try {
                // Sleep makes it more obvious that this is a separate thread
                Thread.sleep(2000);
                // Actually make the new report
                Report newReport = new Report(newTitle);
                ArrayList<LogEntry> logEntries = LogEntry.getLogEntries();
                ArrayList<Integer> logEntryIDs = new ArrayList<>();
                logEntries.forEach(entry -> logEntryIDs.add(entry.getID()));
                newReport.setLogEntryIDs(logEntryIDs);
                // Save it to the user's reports
                user.addReport(newReport);
                // Save the user data
                user.saveUserDataFile();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            System.out.println("Creating " + newTitle + " report done.");
        }
    }

    /**
     * Get the scene to run a specified Report
     * Running a report here means retrieving and displaying all its log entries
     *
     * @param reportToRun The Report to be run
     * @return the Scene that will run the report
     */
    static Scene runReportScene(Report reportToRun) {
        System.out.println("Running report " + reportToRun.toString());

        VBox runLayout = new VBox();
        runLayout.setAlignment(Pos.TOP_CENTER);
        runLayout.setSpacing(10);

        ObservableList<LogEntry> myEntries = FXCollections.observableArrayList();
        reportToRun.getLogEntryIDs().forEach(id -> myEntries.add(LogEntry.getLogEntryByID(id)));
        if (myEntries.size() > 0) {
            ListView<LogEntry> logEntryList = new ListView<>(myEntries);
            runLayout.getChildren().add(logEntryList);
        } else {
            runLayout.getChildren().add(Screen.getLabel("No entries in this report."));
        }

        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(10);

        Button exitReportButton = Screen.getAButton("Exit this report");
        exitReportButton.setOnAction(e -> Screen.goToScreen(Screen.Pages.REPORTS));

        menu.getChildren().addAll(
                exitReportButton,
                Screen.getExitButton("Back to main menu")
        );

        runLayout.getChildren().add(
                menu
        );

        return Screen.getAScene(runLayout);
    }

    private void setLogEntryIDs(ArrayList<Integer> logEntryIDs) {
        this.logEntryIDs = logEntryIDs;
    }

    private ArrayList<Integer> getLogEntryIDs() {
        return this.logEntryIDs;
    }

    public String toString() {
        return runDateTime + " " + title;
    }

}
