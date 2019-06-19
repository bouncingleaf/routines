package jmroy;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

class Preferences {
    /**
     * Build the scene for managing user preferences
     * @return The Scene for managing user preferences
     */
    static Scene getPreferencesScene() {
        final int LABEL_COL = 0;
        final int FIELD_COL = 1;
        final int COLOR_THEME_ROW = 0;

        VBox prefsLayout = new VBox();
        ChoiceBox<Theme> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(Theme.THEMES_SHOWN);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((
                obs, oldVal, newVal) -> switchStylesheets(oldVal, newVal)
        );
        GridPane preferenceGrid = Screen.getAGridPane();
        preferenceGrid.add(Screen.getLabel("Color theme preference:"), LABEL_COL, COLOR_THEME_ROW);
        preferenceGrid.add(choiceBox, FIELD_COL, COLOR_THEME_ROW);

        prefsLayout.getChildren().addAll(
                Screen.getLabel("Preferences"),
                preferenceGrid,
                Screen.getExitButton("Exit Preferences"));

        return Screen.getAScene(prefsLayout);
    }

    static void switchStylesheets(Theme oldVal, Theme newVal) {
        User user = User.getSignedInUser();
        user.setThemePreference(newVal);
        user.saveUserDataFile();
        Database.getDb().upsertUser(user);
        if (Screen.getApplication() != null) {
            // No application when testing, so skip this
            Screen.updateStylesheets(oldVal, newVal);
        }
    }

}
