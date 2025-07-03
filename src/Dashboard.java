package daccounts;

import java.time.LocalDate;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {
    public static void show(Stage primaryStage, String username) {
        // Enforcement: check if today's timetable is completed
        if (!isTodayCompleted(username)) {
            Label lockLabel = new Label("You must complete today's study tasks to access the dashboard.");
            lockLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #ff5555;");
            Button timetableBtn = new Button("Go to Timetable");
            timetableBtn.setOnAction(e -> TimetableManager.show(primaryStage, username));
            VBox vbox = new VBox(30, lockLabel, timetableBtn);
            vbox.setStyle("-fx-background-color: #181c2f;");
            vbox.setPadding(new javafx.geometry.Insets(80));
            vbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene scene = new Scene(vbox, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("D-Accounts Locked");
            primaryStage.show();
            return;
        }

        Label label = new Label("Welcome, " + username + "! Dashboard coming soon.");
        Button financeBtn = new Button("Go to Finance Tracker");
        financeBtn.setOnAction(e -> FinanceTracker.show(primaryStage, username));
        Button subjectBtn = new Button("Manage Subjects");
        subjectBtn.setOnAction(e -> SubjectManager.show(primaryStage, username));
        Button courseBtn = new Button("Manage Courses");
        courseBtn.setOnAction(e -> CourseManager.show(primaryStage, username));
        Button timetableBtn = new Button("Timetable Manager");
        timetableBtn.setOnAction(e -> TimetableManager.show(primaryStage, username));
        Button settingsBtn = new Button("Settings");
        settingsBtn.setOnAction(e -> SettingsManager.show(primaryStage, username));
        VBox vbox = new VBox(20, label, financeBtn, subjectBtn, courseBtn, timetableBtn, settingsBtn);
        vbox.setStyle("-fx-background-color: #181c2f;");
        vbox.setPadding(new javafx.geometry.Insets(40));
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts Dashboard");
        primaryStage.show();
    }

    private static boolean isTodayCompleted(String username) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream("dbconfig.properties")) {
            java.util.Properties props = new java.util.Properties();
            props.load(fis);
            String url = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port") + "/"
                    + props.getProperty("database");
            String dbUser = props.getProperty("username");
            String dbPass = props.getProperty("password");
            java.sql.Connection conn = java.sql.DriverManager.getConnection(url, dbUser, dbPass);
            java.sql.PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            java.sql.ResultSet rs = getUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt(1);
                java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM timetable WHERE user_id = ? AND date = ? AND completed = FALSE");
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                java.sql.ResultSet trs = stmt.executeQuery();
                if (trs.next()) {
                    int incomplete = trs.getInt(1);
                    return incomplete == 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // If error, do not lock out
    }
}
