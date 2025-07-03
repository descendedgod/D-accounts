package daccounts;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Placeholder for first-run DB config or login
        Label label = new Label("Welcome to D-Accounts! (UI coming soon)");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("D-Accounts Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
