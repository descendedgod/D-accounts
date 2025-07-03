package daccounts;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Dashboard {
    public static void show(Stage primaryStage, String username) {
        Label label = new Label("Welcome, " + username + "! Dashboard coming soon.");
        Button financeBtn = new Button("Go to Finance Tracker");
        financeBtn.setOnAction(e -> FinanceTracker.show(primaryStage, username));
        StackPane root = new StackPane(label, financeBtn);
        StackPane.setMargin(financeBtn, new javafx.geometry.Insets(60,0,0,0));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts Dashboard");
        primaryStage.show();
    }
}
