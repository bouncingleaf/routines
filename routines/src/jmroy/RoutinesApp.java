package jmroy;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * RoutinesApp - main entry point for the Routines application
 * @author Jessica Roy
 */
public class RoutinesApp extends Application {
    private static User user;
    private static Stage window;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        window.setTitle("Routines!");
        window.setOnCloseRequest(e -> {
            // "Consume" the default closing behavior so we can close (or not)
            // on our own terms
            e.consume();
            closeProgram();
        });
        window.setScene(getLoginScene());
        window.show();
    }

    private GridPane getAGridPane() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        return pane;
    }

    private Button getAButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: darkslateblue; -fx-text-fill: white;"
        );
        return button;
    }

    private Button getExitButton(String text) {
        Button exitButton = getAButton(text == null ? "Exit" : text);
        exitButton.setOnAction(e -> window.setScene(getMainScene()));
        return exitButton;
    }

    private Scene getLoginScene() {
        GridPane loginLayout = getAGridPane();

        loginLayout.add(new Text("Welcome to the Routines app!"),0, 0, 2, 1);
        loginLayout.add(new Text("Sign in, or sign up as a new user:"),0, 1, 2, 1);

        TextField userNameTextField = new TextField();
        loginLayout.add(new Label("User name:"), 0, 2);
        loginLayout.add(userNameTextField, 1, 2);

        Button signInButton = getAButton("Go!");
        signInButton.setOnAction(event -> signInOnClick(userNameTextField));

        loginLayout.add(signInButton, 0, 4);

        return new Scene(loginLayout, WIDTH, HEIGHT);
    }

    private void signInOnClick(TextField field) {
        String name = field.getText().toLowerCase().replaceAll("[^a-z0-9]", "");
        if (name.length() > 0) {
            if (userFound(name)) {
                user = User.load(name);
                window.setScene(getMainScene());
            } else {
                user = createNewUser(name);
                window.setScene(getNamePromptScene());
            }
        } else {
            System.out.println("Not valid");
        }

    }

    private Scene getNamePromptScene() {
        GridPane namePromptLayout = getAGridPane();

        TextField displayNameTextField = new TextField();
        namePromptLayout.add(new Label("Enter your name (optional):"), 0, 2);
        namePromptLayout.add(displayNameTextField, 1, 2);

        Button newAccountButton = getAButton("Sign up");
        newAccountButton.setOnAction(e -> signUpOnClick(displayNameTextField));

        namePromptLayout.add(newAccountButton, 0, 4);

        return new Scene(namePromptLayout, WIDTH, HEIGHT);

    }

    private void signUpOnClick(TextField field) {
        String name = field.getText().replaceAll("[^a-zA-Z0-9\']"," ");
        user.setName( name.length() > 0 ? name : user.getUserName());
        System.out.println("Saving user " + user.toString());
        user.save();
        window.setScene(getMainScene());
    }

    private Scene getMainScene() {
        // The main menu
        final String[][] MENU_CHOICES = {
                {"Add a routine", "add"},
                {"Manage routines", "manage"},
                {"Preferences", "prefs"}
        };

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);

        HBox menu = new HBox();
        menu.setSpacing(10);
        Stream.of(MENU_CHOICES)
                .map(choice -> {
                    Button button = getAButton(choice[0]);
                    button.setOnAction(e -> handleChoice(choice[1]));
                    return button;
                })
                .forEach(button -> menu.getChildren().add(button));
        mainLayout.getChildren().add(menu);

        mainLayout.getChildren().add(new Text(user.getName() + "'s routines"));
        if (user != null) {
            ObservableList<Routine> myRoutines = FXCollections.observableArrayList(user.getMyRoutines());
            if (myRoutines.size() > 0) {
                ListView<Routine> routinesList = new ListView<>(myRoutines);
                routinesList.setOnMouseClicked(e -> {
                    Routine selected = routinesList.getSelectionModel().getSelectedItem();
                    window.setScene(getRunRoutineScene(selected));
                });
                mainLayout.getChildren().add(routinesList);
            } else {
                mainLayout.getChildren().add(new Text("No routines yet."));
            }
        } else {
            // Should never get here with normal operation
            System.out.println("User was null");
        }

        Scene scene = new Scene(mainLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add(user.getThemePreference().getFilename());
        return scene;
    }

    private Scene getAddRoutineScene() {
        final int LABEL_COL = 0;
        final int FIELD_COL = 1;
        final int ROUTINE_NAME_ROW = 2;
        final int TASK_NAME_ROW = 4;
        final int TASK_LENGTH_ROW = 5;
        final int SAVE_TASK_BUTTON_ROW = 6;

        // Set up the tasks
        ObservableList<Task> myTasks = FXCollections.observableArrayList();

        // The Add Routine page layout
        VBox addRoutineLayout = new VBox();

        // First thing on the page is a grid for the input fields
        GridPane inputFieldsGrid = getAGridPane();
        final TextField routineNameTextField = new TextField();
        inputFieldsGrid.add(new Label("Enter the routine name:"), LABEL_COL, ROUTINE_NAME_ROW);
        inputFieldsGrid.add(routineNameTextField, FIELD_COL, ROUTINE_NAME_ROW);

        final TextField addTaskNameTextField = new TextField();
        inputFieldsGrid.add(new Label("Task Name"), LABEL_COL, TASK_NAME_ROW);
        inputFieldsGrid.add(addTaskNameTextField, FIELD_COL, TASK_NAME_ROW);

        final TextField addTaskLengthTextField = new TextField();
        inputFieldsGrid.add(new Label("Task Length (minutes)"), LABEL_COL, TASK_LENGTH_ROW);
        inputFieldsGrid.add(addTaskLengthTextField, FIELD_COL, TASK_LENGTH_ROW);

        // A "Save task" button
        final Button saveTaskButton = getAButton("Save Task");
        saveTaskButton.setOnAction(e -> {
            String taskName = addTaskNameTextField.getText();
            int taskLength;
            if (taskName.length() > 0) {
                try {
                    taskLength = Integer.parseInt(addTaskLengthTextField.getText());
                } catch (NumberFormatException exception) {
                    taskLength = 0;
                }
                addTaskNameTextField.clear();
                addTaskLengthTextField.clear();
                myTasks.add(taskLength > 0 ? new TimedTask(taskName, taskLength) : new UntimedTask(taskName));
            }
        });
        inputFieldsGrid.add(saveTaskButton, LABEL_COL, SAVE_TASK_BUTTON_ROW);

        // Next up, a list of tasks
        ListView<Task> tasksList = new ListView<>(myTasks);

        // A save button for saving the routine and its tasks
        final Button saveRoutineButton = getAButton("Save routine and tasks");
        saveRoutineButton.setOnAction(e -> {
            String routineName = routineNameTextField.getText();
            if (routineName.length() > 0 && myTasks.size() > 0) {
                Routine newRoutine = new Routine(routineNameTextField.getText());
                myTasks.forEach(newRoutine::addTask);
                user.addRoutine(newRoutine);
                user.save();
                window.setScene(getMainScene());
            } else {
                System.out.println("Required fields not satisfied");
                // Todo: check required fields
            }
        });

        addRoutineLayout.getChildren().addAll(
                inputFieldsGrid,
                new Text("Tasks:"),
                tasksList,
                saveRoutineButton);

        Scene scene = new Scene(addRoutineLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add(user.getThemePreference().getFilename());
        return scene;

    }

    /**
     * Build the scene for managing routines
     * @return The Scene for managing routines
     */
    private Scene getManageRoutinesScene() {
        VBox manageLayout = new VBox();
        manageLayout.getChildren().add(new Text("Manage routines - coming soon"));
        manageLayout.getChildren().add(getExitButton("Exit Manage Routines"));
        Scene scene = new Scene(manageLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add(user.getThemePreference().getFilename());
        return scene;
    }

    private Scene getRunRoutineScene(Routine routineToRun) {
        VBox runLayout = new VBox();
        runLayout.getChildren().add(new Text("Real run routine - coming soon. For now, a simulation.\n"));
        runLayout.getChildren().add(new Text("Running routine " + routineToRun.getTitle() + ":\n"));
        routineToRun.getTasks().forEach(
              task -> runLayout.getChildren().add(new Text (task.toString()))
        );
        runLayout.getChildren().add(getExitButton("Exit " + routineToRun.getTitle()));
        Scene scene = new Scene(runLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add(user.getThemePreference().getFilename());
        return scene;
    }

    /**
     * Build the scene for managing user preferences
     * @return The Scene for managing user preferences
     */
    private Scene getPreferencesScene() {
        VBox prefsLayout = new VBox();
        ChoiceBox cb = new ChoiceBox();
        cb.getItems().addAll(Theme.LIGHT, Theme.DARK);
        cb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            user.setThemePreference((Theme) newVal);
        });
        prefsLayout.getChildren().addAll(
                new Text("Preferences"),
                cb,
                getExitButton("Exit Preferences"));
        Scene scene = new Scene(prefsLayout, WIDTH, HEIGHT);
        scene.getStylesheets().add(user.getThemePreference().getFilename());
        return scene;
    }
    /**
     * Get or create the User whose routines we want to work with
     * @param userName String of the user name to look for
     * @return true if the user is found, false if not
     */
    private static boolean userFound(String userName) {
        // Open the users file, establish a reader, and get the user
        File usersFile = new File(User.USERS_FILE);
        try (
                Scanner userReader = usersFile.exists() ? new Scanner(usersFile) : null
        ) {
            boolean found = false;
            // If we have a username, look for it in the users file
            if (userName != null && userReader != null) {
                while (userReader.hasNextLine() && !found) {
                    found = userReader.nextLine().equals(userName);
                }
            }
            return found;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static User createNewUser(String userName) {
        File usersFile = new File(User.USERS_FILE);
        try (
                PrintWriter userWriter = new PrintWriter(new FileOutputStream(usersFile, true))
        ) {
            User newUser = new User(userName);

            // Save username to the users file
            userWriter.println(newUser.getUserName());

            return newUser;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Process the user's menu choice
     * @param choice A string describing the user's choice
     */
    private void handleChoice(String choice) {
        switch (choice) {
            case "add":
                window.setScene(getAddRoutineScene());
                break;
            case "manage":
                window.setScene(getManageRoutinesScene());
                break;
            case "prefs":
                window.setScene(getPreferencesScene());
                break;
            default:
                // do nothing
                break;
        }
    }

    private static void closeProgram() {
        System.out.println("Closing program.");
        window.close();
    }

    public static void main (String[] args) {

        launch(args);
    }

}
