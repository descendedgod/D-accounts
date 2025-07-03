package daccounts;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DBConfigPrompt {
    public static void show(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField hostField = new TextField("localhost");
        TextField portField = new TextField("3306");
        TextField dbField = new TextField("daccounts");
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();

        grid.add(new Label("MySQL Host:"), 0, 0);
        grid.add(hostField, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(portField, 1, 1);
        grid.add(new Label("Database Name:"), 0, 2);
        grid.add(dbField, 1, 2);
        grid.add(new Label("Username:"), 0, 3);
        grid.add(userField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passField, 1, 4);

        Button saveBtn = new Button("Save & Continue");
        Label status = new Label();
        grid.add(saveBtn, 1, 5);
        grid.add(status, 1, 6);

        saveBtn.setOnAction(e -> {
            DBConfig config = new DBConfig(
                hostField.getText(),
                portField.getText(),
                dbField.getText(),
                userField.getText(),
                passField.getText()
            );
            boolean saved = config.saveToFile("dbconfig.properties");
            if (saved) {
                status.setText("Configuration saved! Please import daccounts_schema.sql into your MySQL server.");
            } else {
                status.setText("Failed to save configuration.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts: Database Setup");
        primaryStage.show();
    }
}
