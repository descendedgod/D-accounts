package daccounts;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.Properties;

public class SettingsManager {
    public static void show(Stage primaryStage, String username) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #181c2f;");

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: #fff;");

        TextField freeStartField = new TextField("18:00");
        TextField freeEndField = new TextField("22:00");
        TextField semesterStartField = new TextField();
        TextField semesterEndField = new TextField();
        freeStartField.setPromptText("Free time start (HH:mm)");
        freeEndField.setPromptText("Free time end (HH:mm)");
        semesterStartField.setPromptText("Semester start date (YYYY-MM-DD)");
        semesterEndField.setPromptText("Semester end date (YYYY-MM-DD)");
        Button saveBtn = new Button("Save Settings");
        Label result = new Label();
        result.setStyle("-fx-text-fill: #fff;");

        saveBtn.setOnAction(e -> {
            try {
                Properties props = new Properties();
                props.setProperty("free_start", freeStartField.getText());
                props.setProperty("free_end", freeEndField.getText());
                props.setProperty("semester_start", semesterStartField.getText());
                props.setProperty("semester_end", semesterEndField.getText());
                try (FileOutputStream fos = new FileOutputStream("settings.properties")) {
                    props.store(fos, "User Settings");
                }
                result.setText("Settings saved!");
            } catch (Exception ex) {
                result.setText("Failed to save settings.");
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> Dashboard.show(primaryStage, username));

        root.getChildren().addAll(title, freeStartField, freeEndField, semesterStartField, semesterEndField, saveBtn, result, backBtn);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Settings");
        primaryStage.show();
    }
}
