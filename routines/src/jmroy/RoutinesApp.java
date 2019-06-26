package jmroy;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * RoutinesApp - main entry point for the Routines application
 *
 * @author Jessica Roy
 */
public class RoutinesApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Screen screen = new Screen(primaryStage, "Routines!");
        Database.createDb(false);
        Screen.setApplication(screen);
        Screen.goToScreen(Screen.Pages.LOGIN);
        screen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
