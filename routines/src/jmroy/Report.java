package jmroy;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Report implements Serializable {
    private String title;
    private LocalDateTime runDateTime;

    private Report(String title) {
        this.runDateTime = LocalDateTime.now();
        this.title = title;
    }

    public String toString() {
        return runDateTime + " " + title;
    }

    /**
     * Creates a new report in a separate thread
     * @param title The title of the report to create
     */
    static void createNewReport(String title) {
        Thread reportThread = new Thread(new CreateReport(title));
        reportThread.start();
    }

    static class CreateReport implements Runnable {
        private String newTitle;

        CreateReport(String newTitle) {
            this.newTitle = newTitle;
        }

        public void run() {
            System.out.println("Thread starting to create report: " + newTitle);
            User user = User.getSignedInUser();
            // Simulate taking some time to make the report...
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            // Actually make the new report
            Report newReport = new Report(newTitle);
            // Save it to the user's reports
            user.addReport(newReport);
            // Save the user data
            user.saveUserDataFile();
            System.out.println("Thread for creating " + newTitle + " report done.");
        }
    }
}
