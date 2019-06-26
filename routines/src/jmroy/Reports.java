package jmroy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

class Reports {


    /**
     * Build the scene for managing reports
     *
     * @return The Scene for managing reports
     */
    static Scene getReportsScene() {
        ObservableList<Report> myReports = FXCollections.observableArrayList(User.getSignedInUser().getMyReports());

        VBox reportsLayout = new VBox();
        reportsLayout.setAlignment(Pos.CENTER);

        // Create the button for generating a new report
        Button newReportButton = new Button();
        newReportButton.setText("Generate a new report");
        newReportButton.setOnAction(e -> Report.createNewReport());

        // Add a label and the new report button
        reportsLayout.getChildren().addAll(
                Screen.getLabel("Reports"),
                newReportButton
        );

        // If there are any reports, display them in a ListView
        // Otherwise, display a message
        if (myReports.size() > 0) {
            ListView<Report> reportsList = new ListView<>(myReports);
            // If the user clicks on a report, run it (i.e., get its log entries)
            reportsList.setOnMouseClicked(e -> {
                Report selected = reportsList.getSelectionModel().getSelectedItem();
                Screen.getApplication().setScene(Report.runReportScene(selected));
            });
            reportsLayout.getChildren().add(reportsList);
        } else {
            reportsLayout.getChildren().add(Screen.getLabel("No reports yet."));
        }

        reportsLayout.getChildren().add(Screen.getExitButton("Exit Reports"));

        return Screen.getAScene(reportsLayout);
    }
}
