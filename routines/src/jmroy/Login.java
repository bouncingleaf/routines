package jmroy;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

class Login {
      static Scene getLoginScene() {
        GridPane loginLayout = Screen.getAGridPane();

        loginLayout.add(Screen.getLabel("Welcome to the Routines app!"),0, 0, 2, 1);
        loginLayout.add(Screen.getLabel("Sign in, or sign up as a new user:"),0, 1, 2, 1);

        TextField userNameTextField = new TextField();
        loginLayout.add(Screen.getLabel("User name:"), 0, 2);
        loginLayout.add(userNameTextField, 1, 2);

        Button signInButton = Screen.getAButton("Go!");
        signInButton.setOnAction(event -> {
            Screen.Pages page = User.signIn(userNameTextField.getText());
            Screen.goToScreen(page);
        });

        loginLayout.add(signInButton, 0, 4);

        return Screen.getAScene(loginLayout);
    }

    static Scene getNamePromptScene() {
        GridPane namePromptLayout = Screen.getAGridPane();

        TextField displayNameTextField = new TextField();
        namePromptLayout.add(Screen.getLabel("Enter your name (optional):"), 0, 2);
        namePromptLayout.add(displayNameTextField, 1, 2);

        Button newAccountButton = Screen.getAButton("Sign up");
        newAccountButton.setOnAction(e -> {
            User.signUp(displayNameTextField.getText());
            Screen.goToScreen(Screen.Pages.MAIN);
        });

        namePromptLayout.add(newAccountButton, 0, 4);

        return Screen.getAScene(namePromptLayout);
    }

}
