package jmroy;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
// Can't import both javafx.util.Duration and java.time.Duration
// import javafx.util.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.time.Duration;

/**
 * The Routine class: a Routine is a list of tasks, with a title.
 * @author Jessica Roy
 */
class Routine implements Serializable {

    // Static variable
    static private Task selectedTask;
    static private Routine selectedRoutine;
    static private TextField routineNameTextField;
    static private TextField addTaskNameTextField;
    static private TextField addTaskLengthTextField;
    static private Timeline timeline;
    static private int currentRunningTask = 0;
    enum TreatMinutesAs{
        MINUTES,
        SECONDS
    }
    static private TreatMinutesAs currentMode = TreatMinutesAs.SECONDS;

    // Instance variables

    private String title = "My Routine";
    private ArrayList<Task> tasks;

    // Constructors
    
    private Routine() {
        this.tasks = new ArrayList<>();
    }

    Routine(String title) {
        this();
        this.title = title;
    }

    // Class methods

    static Scene addRoutineScene(Routine routine) {
        selectedRoutine = routine;
        selectedTask = null;
        final int LABEL_COL = 0;
        final int FIELD_COL = 1;
        final int ROUTINE_NAME_ROW = 2;
        final int TASK_NAME_ROW = 4;
        final int TASK_LENGTH_ROW = 5;
        final int SAVE_TASK_BUTTON_ROW = 6;

        // Set up the tasks
        ObservableList<Task> myTasks = routine == null ? FXCollections.observableArrayList() : FXCollections.observableArrayList(routine.getTasks());

        // The Add Routine page layout
        VBox addRoutineLayout = new VBox();
        addRoutineLayout.setAlignment(Pos.CENTER);

        // First thing on the page is a grid for the input fields
        GridPane inputFieldsGrid = Screen.getAGridPane();
        routineNameTextField = routine == null ? new TextField() : new TextField(routine.getName());
        addTaskNameTextField = selectedTask == null ? new TextField() : new TextField(selectedTask.getName());
        // If the task is empty or untimed, leave the time blank
        // Otherwise, it's a TimedTask, get the time in string form from the TimedTask
        addTaskLengthTextField =
                selectedTask == null || selectedTask instanceof UntimedTask ? new TextField() :
                        new TextField(Integer.toString(((TimedTask) selectedTask).getMinutes()));

        inputFieldsGrid.add(Screen.getLabel("Enter the routine name:"), LABEL_COL, ROUTINE_NAME_ROW);
        inputFieldsGrid.add(routineNameTextField, FIELD_COL, ROUTINE_NAME_ROW);

        inputFieldsGrid.add(Screen.getLabel("Task Name"), LABEL_COL, TASK_NAME_ROW);
        inputFieldsGrid.add(addTaskNameTextField, FIELD_COL, TASK_NAME_ROW);

        inputFieldsGrid.add(Screen.getLabel("Task Length (minutes)"), LABEL_COL, TASK_LENGTH_ROW);
        inputFieldsGrid.add(addTaskLengthTextField, FIELD_COL, TASK_LENGTH_ROW);

        // A "Save task" button
        final Button saveTaskButton = Screen.getAButton("Save task");
        saveTaskButton.setOnAction(e -> saveTask(myTasks));

        // Clear input button
        final Button clearTaskButton = Screen.getAButton("Clear");
        clearTaskButton.setOnAction(e -> clearTask());

        // Delete task button
        final Button deleteTaskButton = Screen.getAButton("Delete task");
        deleteTaskButton.setOnAction(e -> {
            myTasks.remove(selectedTask);
            clearTask();
        });

        HBox taskButtons = new HBox();
        taskButtons.setSpacing(10);
        taskButtons.getChildren().addAll(
                saveTaskButton,
                clearTaskButton,
                deleteTaskButton
        );
        inputFieldsGrid.add(taskButtons, LABEL_COL, SAVE_TASK_BUTTON_ROW, 2, 1);

        // Next up, a list of tasks, click to select one
        ListView<Task> tasksList = new ListView<>(myTasks);
        tasksList.setOnMouseClicked(e -> selectTask(tasksList, routine));

        // A save button for saving the routine and its tasks
        final Button saveRoutineButton = Screen.getAButton("Save routine and tasks");
        saveRoutineButton.setOnAction(e -> {
            saveAll(myTasks);
            Screen.goToScreen(Screen.Pages.MAIN);
        });

        // An exit button, to exit without saving
        final Button exitButton = Screen.getExitButton("Exit without saving");

        HBox routineButtons = new HBox();
        routineButtons.setSpacing(10);
        routineButtons.getChildren().addAll(
                saveRoutineButton,
                exitButton
        );

        addRoutineLayout.getChildren().addAll(
                inputFieldsGrid,
                Screen.getLabel("Tasks (select one to edit it):"),
                tasksList,
                routineButtons);

        return Screen.getAScene(addRoutineLayout);
    }

    private static void saveTask(ObservableList<Task> myTasks) {
        String taskName = addTaskNameTextField.getText();
        int taskLength;
        if (taskName.length() > 0) {
            try {
                taskLength = Integer.parseInt(addTaskLengthTextField.getText());
                // no negative task length
                if (taskLength < 0) {
                    taskLength = 0;
                }
            } catch (NumberFormatException exception) {
                taskLength = 0;
            }
            Task newTask = taskLength > 0 ? new TimedTask(taskName, taskLength) : new UntimedTask(taskName);
            if (selectedTask == null) {
                myTasks.add(newTask);
            } else {
                myTasks.set(myTasks.indexOf(selectedTask), newTask);
            }
            clearTask();
        }
    }

    private static void clearTask() {
        addTaskLengthTextField.clear();
        addTaskNameTextField.clear();
        selectedTask = null;
    }

    private static void selectTask(ListView<Task> tasksList, Routine routine) {
        selectedTask = tasksList.getSelectionModel().getSelectedItem();
        routineNameTextField.setText(routine == null ? "" : routine.getName());
        addTaskNameTextField.setText(selectedTask == null ? "" : selectedTask.getName());
        // If the task is empty or untimed, leave the time blank
        // Otherwise, it's a TimedTask, get the time in string form from the TimedTask
        addTaskLengthTextField .setText(selectedTask == null || selectedTask instanceof UntimedTask ? "" :
                Integer.toString(((TimedTask) selectedTask).getMinutes()));
    }

    private static void saveAll(ObservableList<Task> myTasks) {
        User user = User.getSignedInUser();
        String routineName = routineNameTextField.getText();
        Routine newRoutine = new Routine(routineName.length() > 0 ? routineName : "My Routine");
        myTasks.forEach(newRoutine::addTask);
        if (selectedRoutine == null) {
            // Add a new routine
            user.addRoutine(newRoutine);
        } else {
            // Update an existing routine
            ArrayList<Routine> myRoutines = user.getMyRoutines();
            myRoutines.set(myRoutines.indexOf(selectedRoutine),newRoutine);
            user.setMyRoutines(myRoutines);
        }
        user.saveUserDataFile();
    }

    /**
     * Build the scene for managing routines
     * @return The Scene for managing routines
     */
    static Scene manageRoutinesScene() {
        VBox manageLayout = new VBox();
        manageLayout.setAlignment(Pos.CENTER);

        manageLayout.getChildren().addAll(
                Screen.getLabel("Managing " + User.getSignedInUser().getName() + "'s routines"),
                Screen.getLabel("Choose a routine to edit:")
        );
        if (User.getSignedInUser() != null) {
            ObservableList<Routine> myRoutines = FXCollections.observableArrayList(User.getSignedInUser().getMyRoutines());
            if (myRoutines.size() > 0) {
                ListView<Routine> routinesList = new ListView<>(myRoutines);
                routinesList.setOnMouseClicked(e -> {
                    Routine selected = routinesList.getSelectionModel().getSelectedItem();
                    Screen.getApplication().setScene(Routine.addRoutineScene(selected));
                });
                manageLayout.getChildren().add(routinesList);
            } else {
                manageLayout.getChildren().add(Screen.getLabel("No routines yet."));
            }
        } else {
            // Should never get here with normal operation
            System.out.println("User was null");
        }

        manageLayout.getChildren().add(Screen.getExitButton("Exit Manage Routines"));

        return Screen.getAScene(manageLayout);
    }

    /**
     * Get the scene to run a specified Routine
     * Timer code based on:
     * https://asgteach.com/wp-content/uploads/2015/04/FXTimerBinding.java
     *
     * @param routineToRun The Routine to be run
     * @return the Scene that will run the routine
     */
    static Scene runRoutineScene(Routine routineToRun) {
        ArrayList<Task> tasks = routineToRun.getTasks();
        VBox runLayout = new VBox();
        runLayout.setAlignment(Pos.CENTER);
        runLayout.setSpacing(10);

        // If there are no tasks in this routine, just show the exit button
        if (tasks.size() == 0) {
            runLayout.getChildren().add(Screen.getExitButton("Exit " + routineToRun.getTitle()));
            return Screen.getAScene(runLayout);
        }

        // Otherwise, run the first task
        currentRunningTask = 0;

        // Add the task to the task layout
        VBox taskLayout = new VBox();
        taskLayout.setAlignment(Pos.CENTER);
        taskLayout.setSpacing(10);
        taskLayout.getChildren().add(
                singleTimer(tasks.get(currentRunningTask))
        );

        // Create the "done" button - if there are tasks left, moves on to the next
        // If no tasks left, button ends the routine
        Button doneButton = new Button();
        doneButton.setText("Done with this task");
        doneButton.setOnAction(e -> {
            currentRunningTask++;
            taskLayout.getChildren().clear();
            taskLayout.getChildren().add(
                    currentRunningTask < tasks.size() ?
                            singleTimer(tasks.get(currentRunningTask)) :
                            Screen.getExitButton("Finished " + routineToRun.getTitle())
            );
        });

        Button demoButton = new Button();
        demoButton.setText("Demo mode");
        demoButton.setOnAction(event -> demoModeScene(runLayout, routineToRun));

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setMinHeight(20);
        footer.setSpacing(10);

        footer.getChildren().addAll(
                demoButton,
                Screen.getExitButton("Exit this routine")
        );

        // Add the task layout and done button to the run layout
        runLayout.getChildren().addAll(
                taskLayout,
                doneButton,
                footer
        );

        return Screen.getAScene(runLayout);
    }

    /**
     * Get the scene to run a specified Routine - Demo mode
     * Timer code based on:
     * https://asgteach.com/wp-content/uploads/2015/04/FXTimerBinding.java
     *
     * @param routineToRun The Routine to be run
     */
    private static void demoModeScene(VBox mainLayout, Routine routineToRun) {
        routineToRun.getTasks().forEach(
                task -> mainLayout.getChildren().addAll(
                        Screen.getLabel(task.toString()),
                        singleTimer(task))
        );
    }

    private static VBox singleTimer(Task task) {
        VBox runLayout = new VBox();
        runLayout.setAlignment(Pos.CENTER);

        if (task instanceof TimedTask) {
            int time = ((TimedTask) task).getMinutes();
            Duration duration = currentMode == TreatMinutesAs.MINUTES ?
                    Duration.ofMinutes(time) :
                    Duration.ofSeconds(time);
            Label timeRemainingText = new Label();
            IntegerProperty timeSeconds = new SimpleIntegerProperty((int) duration.getSeconds());

            // Bind the timeRemainingText text property to the timeSeconds property
            timeRemainingText.textProperty().bind(timeSeconds.asString());
            timeRemainingText.setTextFill(Color.RED);
            timeRemainingText.setStyle("-fx-font-size: 6em;");

            Button startButton = new Button();
            startButton.setText("Start task: " + task.getName());
            startButton.setOnAction(event -> {
                if (timeline != null) {
                    timeline.stop();
                }
                timeSeconds.set(time);
                timeline = new Timeline();
                timeline.getKeyFrames().add(
                        new KeyFrame(javafx.util.Duration.seconds(duration.getSeconds() + 1),
                                new KeyValue(timeSeconds, 0)));
                timeline.playFromStart();
            });
            runLayout.getChildren().addAll(
                    startButton,
                    timeRemainingText
            );
        } else {
            runLayout.getChildren().add(
                    Screen.getLabel("Task: " + task.getName() + " (untimed)")
            );
        }

        return runLayout;
    }


    // Methods

    public String getName() {
        return title;
    }

    String getTitle() { return title; }

    int numberOfTasks() {
        return this.tasks.size();
    }
    
    void addTask(Task task) {
        this.tasks.add(task);
    }

    private ArrayList<Task> getTasks() { return tasks; }

    @Override
    public String toString() {
        return this.getTitle();
    }



}
