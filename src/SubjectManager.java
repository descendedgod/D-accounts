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

public class SubjectManager {
    public static void show(Stage primaryStage, String username) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #181c2f;");

        Label title = new Label("Subject Manager");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: #fff;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject name");
        Button addBtn = new Button("Add Subject");
        Label result = new Label();
        result.setStyle("-fx-text-fill: #fff;");

        TableView<SubjectRecord> table = new TableView<>();
        TableColumn<SubjectRecord, String> nameCol = new TableColumn<>("Subject Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subjectName"));
        table.getColumns().add(nameCol);
        table.setItems(getSubjects(username));
        table.setPrefHeight(200);

        addBtn.setOnAction(e -> {
            String subject = subjectField.getText().trim();
            if (!subject.isEmpty() && addSubject(username, subject)) {
                result.setText("Subject added!");
                table.setItems(getSubjects(username));
                subjectField.clear();
            } else {
                result.setText("Failed to add subject.");
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> Dashboard.show(primaryStage, username));

        root.getChildren().addAll(title, subjectField, addBtn, result, table, backBtn);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Subject Manager");
        primaryStage.show();
    }

    public static class SubjectRecord {
        private String subjectName;
        public SubjectRecord(String subjectName) { this.subjectName = subjectName; }
        public String getSubjectName() { return subjectName; }
    }

    private static ObservableList<SubjectRecord> getSubjects(String username) {
        ObservableList<SubjectRecord> records = FXCollections.observableArrayList();
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
                PreparedStatement stmt = conn.prepareStatement("SELECT subject_name FROM subjects WHERE user_id = ?");
                stmt.setInt(1, userId);
                ResultSet srs = stmt.executeQuery();
                while (srs.next()) {
                    records.add(new SubjectRecord(srs.getString(1)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    private static boolean addSubject(String username, String subject) {
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
                PreparedStatement insert = conn.prepareStatement("INSERT INTO subjects (user_id, subject_name) VALUES (?, ?)");
                insert.setInt(1, userId);
                insert.setString(2, subject);
                insert.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
