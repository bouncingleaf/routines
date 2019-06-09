package jmroy;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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

    private Scene getLoginScene() {
        GridPane loginLayout = getAGridPane();

        loginLayout.add(new Text("Welcome to the Routines app!"),0, 0, 2, 1);
        loginLayout.add(new Text("Enter your username and sign in, or sign up as a new user:"),0, 1, 2, 1);

        TextField userNameTextField = new TextField();
        loginLayout.add(new Label("User name:"), 0, 2);
        loginLayout.add(userNameTextField, 1, 2);

        Button signInButton = new Button();
        signInButton.setText("Go!");
        signInButton.setOnAction(event -> {
            String name = userNameTextField.getText().toLowerCase().replaceAll("[^a-z0-9]", "");
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
        });

        loginLayout.add(signInButton, 0, 4);

        return new Scene(loginLayout, WIDTH, HEIGHT);
    }

    private Scene getNamePromptScene() {
        GridPane namePromptLayout = getAGridPane();

        TextField displayNameTextField = new TextField();
        namePromptLayout.add(new Label("Enter your name (optional):"), 0, 2);
        namePromptLayout.add(displayNameTextField, 1, 2);

        Button newAccountButton = new Button();
        newAccountButton.setText("Sign up");
        newAccountButton.setOnAction(event -> {
            String name = displayNameTextField.getText().replaceAll("[^a-zA-Z0-9\']"," ");
            user.setName( name.length() > 0 ? name : user.getUserName());
            System.out.println("Saving user " + user.toString());
            user.save();
            window.setScene(getMainScene());
        });

        namePromptLayout.add(newAccountButton, 0, 4);

        return new Scene(namePromptLayout, WIDTH, HEIGHT);

    }

    private Scene getMainScene() {
        // The main menu
        final String[][] MENU_CHOICES = {
                {"Add a routine", "add"},
                {"Manage routines", "manage"},
                {"Run a routine", "run"},
                {"Quit", "quit"}
        };

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);

        if (user != null) {
            ArrayList myRoutines = user.getMyRoutines();
            if (myRoutines.size() > 0) {
                Stream.of(myRoutines)
                        .map(choice -> new Label(choice.toString()))
                        .forEach(label -> mainLayout.getChildren().add(label));
            } else {
                mainLayout.getChildren().add(new Text("No routines yet."));
            }
        } else {
            // Should never get here with normal operation
            System.out.println("User was null");
        }

        Stream.of(MENU_CHOICES)
                .map(choice -> {
                    Button b = new Button(choice[0]);
                    b.setOnAction(e -> onMenu(choice[1], e));
                    return b;
                })
                .forEach(button -> mainLayout.getChildren().add(button));

        return new Scene(mainLayout, WIDTH, HEIGHT);
    }

    private Scene getAddRoutineScene() {
        // Set up the tasks
        ObservableList<Task> myTasks = FXCollections.observableArrayList();
        // Include a dummy task for now
        myTasks.add(new UntimedTask("testing"));

        // The Add Routine page layout
        VBox addRoutineLayout = new VBox();

        // First thing on the page is a grid for the routine name
        GridPane routineNameLayout = getAGridPane();
        final TextField routineNameTextField = new TextField();
        routineNameLayout.add(new Label("Enter the routine name:"), 0, 2);
        routineNameLayout.add(routineNameTextField, 1, 2);

        final Button routineSaveButton = new Button();
        routineSaveButton.setText("Save routine and tasks");
        routineSaveButton.setOnAction(e -> {
            String routineName = routineNameTextField.getText();
            if (routineName.length() > 0 && myTasks.size() > 0) {
                Routine newRoutine = new Routine(routineNameTextField.getText());
                myTasks.forEach(task -> newRoutine.addTask(task));
                user.addRoutine(newRoutine);
                user.save();
                window.setScene(getMainScene());
            } else {
                // TODO: required fields
            }
        });
        routineNameLayout.add(routineSaveButton,0, 3);

        addRoutineLayout.getChildren().add(routineNameLayout);

        // Next up, a list of tasks
        addRoutineLayout.getChildren().add(new Text("Tasks:"));

        ListView<Task> listView = new ListView<>(myTasks);
        listView.setOnMouseClicked(e -> System.out.println("Mouse " + e));
        addRoutineLayout.getChildren().add(listView);

//        myTasks.stream()
//                .map(task -> {
//                    HBox taskBox = new HBox();
//                    Button b = new Button();
//                    b.setText("edit");
//                    b.setOnAction(e -> System.out.println("Update " + task.getName()));
//                    taskBox.getChildren().addAll(
//                            new Text(task.getName().concat("\t")),
//                            new Text(task.getTimeForDisplay()),
//                            b
//                    );
//                    return taskBox;
//                })
//                .forEach(box -> addRoutineLayout.getChildren().add(box));

        GridPane newTaskLayout = getAGridPane();

        final TextField addTaskNameTextField = new TextField();
        newTaskLayout.add(new Label("Task Name"), 0, 2);
        newTaskLayout.add(addTaskNameTextField, 1, 2);

        final TextField addTaskLengthTextField = new TextField();
        newTaskLayout.add(new Label("Task Length"), 0, 3);
        newTaskLayout.add(addTaskLengthTextField, 1, 3);

        final Button saveTaskButton = new Button("Save Task");
        saveTaskButton.setOnAction(e -> {
            String taskName = addTaskNameTextField.getText();
            int length;
            Task newTask;
            try {
                length = Integer.parseInt(addTaskLengthTextField.getText());
            }
            catch (NumberFormatException exception) {
                length = 0;
            }
            if (length > 0) {
                newTask = new TimedTask(taskName, length);
            } else {
                newTask = new UntimedTask(taskName);
            }
            System.out.println("Adding " + newTask.toString());
            myTasks.add(newTask);
            addRoutineLayout.getChildren().remove(newTaskLayout);
        });
        newTaskLayout.add(saveTaskButton, 0, 4);

        final Button addTaskButton = new Button("Add New Task");
        addTaskButton.setOnAction(e -> addRoutineLayout.getChildren().add(newTaskLayout));

        addRoutineLayout.getChildren().add(addTaskButton);

        return new Scene(addRoutineLayout, WIDTH, HEIGHT);

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
     * @param event The event associated with the button click
     */
    private void onMenu(String choice, ActionEvent event) {
        switch (choice) {
            case "add":
                System.out.println("Add routines");
                window.setScene(getAddRoutineScene());
                break;
            case "manage":
                System.out.println("Manage routines");
                break;
            case "run":
                System.out.println("Run a routine");
                break;
            case "quit":
                System.out.println("Exiting");
                closeProgram();
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

//    private void addNewRoutine(ArrayList<Routine> myRoutines, Routine newRoutine) {
//        if (myRoutines == null) {
//            myRoutines = new ArrayList<>();
//        }
//        if (routineName.length() > 0 ) {
//            Routine newRoutine = new Routine(routineName);
//            if (newRoutine != null) {
////               newRoutine.addTasksToRoutine(input);
//                myRoutines.add(newRoutine);
//                user.setMyRoutines(myRoutines);
//                user.save();
//            }
//        }
//    private static Routine createNewRoutine(Scanner input) {
//        System.out.println("Enter the name of the routine (e.g. 'My morning routine'): ");
//        String routineName = input.nextLine();
//        if (routineName.length() > 0 ) {
//            Routine newRoutine = new Routine(routineName);
//            return newRoutine;
//        } else {
//            return null;
//        }
//    }

    /**
     * Prompts for a user's username, gets the input, looks up the user in the
     * users file. If found, loads the user's data. If not found, starts a
     * new user. Either way (unless there's an error or no username entered),
     * establishes the User object, and greets the user.
     * @param input Scanner for the user's input
     * @return the selected User, or null if one is not selected
     */
/*
    private static User getUser(Scanner input) {
        // Get the user whose routines we want to work with
        File usersFile = new File(User.USERS_FILE);
        // Establish readers and writers to the users file, and go get the user
        try (
                Scanner userReader = usersFile.exists() ? new Scanner(usersFile) : null;
                PrintWriter userWriter = new PrintWriter(new FileOutputStream(usersFile, true))
        ) {
            User newUser;
            String userName = User.getValidUserName(input);
            if (userName == null) {
                return null;
            }
            // Look for the username in the users file
            boolean found = false;
            if (userReader != null) {
                while (userReader.hasNextLine() && !found) {
                    found = userReader.nextLine().equals(userName);
                }
            }

            if (found) {
                newUser = User.load(userName);
            } else {
                System.out.println("Greetings new user. Enter your name (optional): ");

                // Not the best sanitizing but it'll do
                String name = input.nextLine().replaceAll("[^a-zA-Z0-9\']"," ");
                newUser = new User(userName, name);

                // Save username to the users file
                userWriter.println(newUser.getUserName());

                // Initial save of user data
                newUser.save();
            }
            return newUser;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
*/

//    /**
//     * The main menu loop for the program. Displays the menu, gets the user's choice,
//     * and handles the user's choice accordingly. Loop continues until the user
//     * chooses to exit.
//     * @param input Scanner for the user's input
//     */
//    private static void mainMenuLoop(Scanner input, User user) {
//		// The main menu
//		final String MENU =
//                "\n1. Add a routine\n" +
//                "2. Manage routines\n" +
//                "3. Run a routine\n" +
//            	"Anything else to quit.\n" +
//                "Choice: ";
//
//		boolean done = false;
//		int choice;
//
//		while (!done) {
//			// Show the menu, list the user's routines below it
//            System.out.println("\nHome\n");
//            user.listRoutines();
//			System.out.println(MENU);
//
//			// Get the user's choice
//			try {
//                String inputValue = input.nextLine();
//                if (inputValue.length() == 0) {
//                    done = true;
//                } else if (inputValue.toLowerCase() == "m") {
//                } else {
//                    choice = Integer.parseInt(inputValue);
//                    switch (choice) {
//                        case 1:
//                            Routine newRoutine = createNewRoutine(input);
//                            if (newRoutine != null) {
//                                newRoutine.addTasksToRoutine(input);
//                                user.addRoutine(newRoutine);
//                                user.save();
//                            }
//                            break;
//                        case 2:
//                            try {
//                                user.selectRoutine(input, "Choose a routine to edit: ").edit(input);
//                                user.save();
//                            }
//                            catch (InvalidSelectionException e) {
//                                System.out.println(e.getMessage());
//                            }
//                            break;
//                        case 3:
//                            try {
//                                user.selectRoutine(input, "Choose a routine to run: ").run();
//                                user.save();
//                            }
//                            catch (InvalidSelectionException e) {
//                                System.out.println(e.getMessage());
//                            }
//                            break;
//                        default:
//                            done = true;
//                    }
//                }
//			} catch (NumberFormatException e) {
//				// Exit for non-integer input
//                done = true;
//			}
//		}
//	}
//
//    /**
//     * The main method of the program. Loops on displaying a menu and accepting and handling
//     * a menu choice from the user, until the user chooses to quit the application.
//     * @param args command line arguments, not expecting any
//     */
//    public static void main(String[] args) {
//        // Scanner for getting user input
//        Scanner input = new Scanner(System.in);
//
////        // Display an application title
////        System.out.println("Routines!\n");
//
//        // Get the user
//        User user = getUser(input);
//
//        if (user == null) {
//            // Something went wrong
//            System.out.println("Goodbye.");
//        } else {
//            // Display the menu, get the user's choice, handle it
//            mainMenuLoop(input, user);
//
//            // The user has opted to quit. Close the input and say goodbye.
//            System.out.printf("Done. See you next time, %s!\n\n", user.getName());
//        }
//        input.close();
//        System.exit(0);
//    }

}
