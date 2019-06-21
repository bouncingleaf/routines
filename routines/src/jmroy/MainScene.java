package jmroy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.stream.Stream;

class MainScene {

    static Scene getMainScene() {
         class MenuItem {
            private String itemName;
            private Screen.Pages itemPage;

            private MenuItem(String name, Screen.Pages page) {
                this.itemName = name;
                this.itemPage = page;
            }
        }

        // The main menu
        final MenuItem[] MENU_CHOICES = {
                new MenuItem("Add a routine", Screen.Pages.ADD),
                new MenuItem("Manage routines", Screen.Pages.MANAGE),
                new MenuItem("Preferences", Screen.Pages.PREFS)
        };

        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(10);

        HBox menu = new HBox();
        menu.setSpacing(10);
        menu.setAlignment(Pos.CENTER);
        Stream.of(MENU_CHOICES)
                .map(choice -> {
                    Button button = Screen.getAButton(choice.itemName);
                    button.setOnAction(e -> Screen.goToScreen(choice.itemPage));
                    return button;
                })
                .forEach(button -> menu.getChildren().add(button));
        mainLayout.getChildren().add(menu);

        if (User.getSignedInUser() != null) {
            mainLayout.getChildren().add(Screen.getLabel(User.getSignedInUser().getName() + "'s routines"));
            ObservableList<Routine> myRoutines = FXCollections.observableArrayList(User.getSignedInUser().getMyRoutines());
            if (myRoutines.size() > 0) {
                ListView<Routine> routinesList = new ListView<>(myRoutines);
                routinesList.setOnMouseClicked(e -> {
                    Routine selected = routinesList.getSelectionModel().getSelectedItem();
                    Screen.getApplication().setScene(Routine.runRoutineScene(selected));
                });
                mainLayout.getChildren().add(routinesList);
            } else {
                mainLayout.getChildren().add(Screen.getLabel("No routines yet."));
            }
        } else {
            // Should never get here with normal operation
            System.out.println("User was null");
        }

        return Screen.getAScene(mainLayout);
    }

}
