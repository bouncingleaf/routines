package jmroy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class Screen {
    private static Screen application;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    enum Pages {
        MAIN,
        LOGIN,
        NAME,
        ADD,
        MANAGE,
        PREFS,
        ERROR
    }

    private Stage window;

    Screen (Stage window, String title) {
        this.window = window;
        window.setTitle(title);
        window.setOnCloseRequest(e -> {
            // "Consume" the default closing behavior so we can close (or not)
            // on our own terms
            e.consume();
            closeProgram();
        });
    }

    static Screen getApplication() {
        return application;
    }
    static void setApplication(Screen screen) {
        application = screen;
    }

    private void closeProgram() {
        System.out.println("Closing program.");
        window.close();
    }

    static Scene getAScene(Pane layout) {
        Scene scene = new Scene(layout, Screen.WIDTH, Screen.HEIGHT);
        scene.getStylesheets().add(User.getSignedInUser() == null ? Theme.DEFAULT.getFilename() : User.getStylesheet());
        return scene;
    }

    static GridPane getAGridPane() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        return pane;
    }

    static Button getAButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        return button;
    }

    static Button getExitButton(String text) {
        Button exitButton = getAButton(text == null ? "Exit" : text);
        exitButton.setOnAction(e -> goToScreen(Pages.MAIN));
        return exitButton;
    }

    static Label getLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text");
        return label;
    }

    static void updateStylesheets(Theme oldVal, Theme newVal) {
        Scene scene = getApplication().getScene();
        if (scene.getStylesheets().size() > 0 && oldVal != null) {
            scene.getStylesheets().remove(oldVal.getFilename());
        }
        scene.getStylesheets().add(newVal.getFilename());
    }

    private static void go(Scene scene) {
        getApplication().setScene(scene);
    }

    static void goToScreen(Pages page) {
        switch(page) {
            case MAIN:
                go(MainScene.getMainScene());
                break;
            case LOGIN:
                go(Login.getLoginScene());
                break;
            case NAME:
                go(Login.getNamePromptScene());
                break;
            case ADD:
                go(Routine.addRoutineScene(null));
                break;
            case MANAGE:
                go(Routine.manageRoutinesScene());
                break;
            case PREFS:
                go(Preferences.getPreferencesScene());
                break;
        }
    }

    void setScene(Scene scene) {
        window.setScene(scene);
    }
    private Scene getScene() {
        return window.getScene();
    }

    void show(){
        window.show();
    }

}
