package daccounts;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {
    public static void show(Stage primaryStage, String username) {
        Label label = new Label("Welcome, " + username + "! Dashboard coming soon.");
        Button financeBtn = new Button("Go to Finance Tracker");
        financeBtn.setOnAction(e -> FinanceTracker.show(primaryStage, username));
        Button subjectBtn = new Button("Manage Subjects");
        subjectBtn.setOnAction(e -> SubjectManager.show(primaryStage, username));
        Button courseBtn = new Button("Manage Courses");
        courseBtn.setOnAction(e -> CourseManager.show(primaryStage, username));
        VBox vbox = new VBox(20, label, financeBtn, subjectBtn, courseBtn);
        vbox.setStyle("-fx-background-color: #181c2f;");
        vbox.setPadding(new javafx.geometry.Insets(40));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts Dashboard");
        primaryStage.show();
    }
}
