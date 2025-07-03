package daccounts;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        File configFile = new File("dbconfig.properties");
        if (!configFile.exists()) {
            DBConfigPrompt.show(primaryStage);
            return;
        }
        // Show login screen after DB config
        LoginScreen.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
