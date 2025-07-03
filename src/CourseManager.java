package daccounts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

public class CourseManager {
    public static void show(Stage primaryStage, String username) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #181c2f;");

        Label title = new Label("Course Manager");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: #fff;");

        TextField courseField = new TextField();
        courseField.setPromptText("Enter course name");
        Button addBtn = new Button("Add Course");
        Label result = new Label();
        result.setStyle("-fx-text-fill: #fff;");

        TableView<CourseRecord> table = new TableView<>();
        TableColumn<CourseRecord, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));
        table.getColumns().add(nameCol);
        table.setItems(getCourses(username));
        table.setPrefHeight(200);

        addBtn.setOnAction(e -> {
            String course = courseField.getText().trim();
            if (!course.isEmpty() && addCourse(username, course)) {
                result.setText("Course added!");
                table.setItems(getCourses(username));
                courseField.clear();
            } else {
                result.setText("Failed to add course.");
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> Dashboard.show(primaryStage, username));

        root.getChildren().addAll(title, courseField, addBtn, result, table, backBtn);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Course Manager");
        primaryStage.show();
    }

    public static class CourseRecord {
        private String courseName;
        public CourseRecord(String courseName) { this.courseName = courseName; }
        public String getCourseName() { return courseName; }
    }

    private static ObservableList<CourseRecord> getCourses(String username) {
        ObservableList<CourseRecord> records = FXCollections.observableArrayList();
        try (FileInputStream fis = new FileInputStream("dbconfig.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String url = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port") + "/" + props.getProperty("database");
            String dbUser = props.getProperty("username");
            String dbPass = props.getProperty("password");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rs = getUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt(1);
                PreparedStatement stmt = conn.prepareStatement("SELECT course_name FROM courses WHERE user_id = ?");
                stmt.setInt(1, userId);
                ResultSet crs = stmt.executeQuery();
                while (crs.next()) {
                    records.add(new CourseRecord(crs.getString(1)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    private static boolean addCourse(String username, String course) {
        try (FileInputStream fis = new FileInputStream("dbconfig.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String url = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port") + "/" + props.getProperty("database");
            String dbUser = props.getProperty("username");
            String dbPass = props.getProperty("password");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rs = getUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt(1);
                PreparedStatement insert = conn.prepareStatement("INSERT INTO courses (user_id, course_name) VALUES (?, ?)");
                insert.setInt(1, userId);
                insert.setString(2, course);
                insert.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
