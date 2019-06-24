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
     * @return The Scene for managing reports
     */
    static Scene getReportsScene() {
        VBox reportsLayout = new VBox();
        reportsLayout.setAlignment(Pos.CENTER);

        Button newReportButton = new Button();
        newReportButton.setText("Generate a new report");
        newReportButton.setOnAction(e -> Report.createNewReport("My Report"));

        reportsLayout.getChildren().addAll(
                Screen.getLabel("Reports"),
                newReportButton
        );

        ObservableList<Report> myReports = FXCollections.observableArrayList(User.getSignedInUser().getMyReports());
        if (myReports.size() > 0) {
            ListView<Report> reportsList = new ListView<>(myReports);
            reportsList.setOnMouseClicked(e -> {
                Report selected = reportsList.getSelectionModel().getSelectedItem();
                System.out.println("Would display report here: " + selected.toString());
//                Screen.getApplication().setScene(Report.runReportScene(selected));
            });
            reportsLayout.getChildren().add(reportsList);
        } else {
            reportsLayout.getChildren().add(Screen.getLabel("No reports yet."));
        }

        reportsLayout.getChildren().add(Screen.getExitButton("Exit Reports"));

        return Screen.getAScene(reportsLayout);
    }
}
