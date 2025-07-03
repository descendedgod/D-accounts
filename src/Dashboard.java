package daccounts;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Dashboard {
    public static void show(Stage primaryStage, String username) {
        Label label = new Label("Welcome, " + username + "! Dashboard coming soon.");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts Dashboard");
        primaryStage.show();
    }
}
