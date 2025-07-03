package daccounts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableRow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.time.*;

public class TimetableManager {
    public static void show(Stage primaryStage, String username) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #181c2f;");

        Label title = new Label("Timetable Manager");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: #fff;");

        Button generateBtn = new Button("Generate Timetable");
        Label result = new Label();
        result.setStyle("-fx-text-fill: #fff;");

        TableView<TimetableRecord> table = new TableView<>();
        TableColumn<TimetableRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));
        TableColumn<TimetableRecord, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subjectName"));
        TableColumn<TimetableRecord, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));
        TableColumn<TimetableRecord, String> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("startTime"));
        TableColumn<TimetableRecord, String> stopCol = new TableColumn<>("Stop Time");
        stopCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stopTime"));
        TableColumn<TimetableRecord, Boolean> completedCol = new TableColumn<>("Completed");
        completedCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("completed"));
        table.getColumns().addAll(dateCol, subjectCol, courseCol, startCol, stopCol, completedCol);
        table.setItems(getTimetable(username));
        table.setPrefHeight(250);

        table.setRowFactory(tv -> {
            TableRow<TimetableRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    TimetableRecord rec = row.getItem();
                    if (!rec.getCompleted()) {
                        markCompleted(username, rec);
                        table.setItems(getTimetable(username));
                    }
                }
            });
            return row;
        });

        generateBtn.setOnAction(e -> {
            boolean ok = generateTimetable(username);
            if (ok) {
                result.setText("Timetable generated for the next 7 days!");
                table.setItems(getTimetable(username));
            } else {
                result.setText("Failed to generate timetable.");
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> Dashboard.show(primaryStage, username));

        root.getChildren().addAll(title, generateBtn, result, table, backBtn);
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Timetable Manager");
        primaryStage.show();
    }

    public static class TimetableRecord {
        private String date, subjectName, courseName, startTime, stopTime;
        private boolean completed;
        public TimetableRecord(String date, String subjectName, String courseName, String startTime, String stopTime, boolean completed) {
            this.date = date; this.subjectName = subjectName; this.courseName = courseName; this.startTime = startTime; this.stopTime = stopTime; this.completed = completed;
        }
        public String getDate() { return date; }
        public String getSubjectName() { return subjectName; }
        public String getCourseName() { return courseName; }
        public String getStartTime() { return startTime; }
        public String getStopTime() { return stopTime; }
        public boolean getCompleted() { return completed; }
    }

    private static ObservableList<TimetableRecord> getTimetable(String username) {
        ObservableList<TimetableRecord> records = FXCollections.observableArrayList();
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
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT t.date, s.subject_name, c.course_name, t.start_time, t.stop_time, t.completed " +
                    "FROM timetable t " +
                    "LEFT JOIN subjects s ON t.subject_id = s.id " +
                    "LEFT JOIN courses c ON t.course_id = c.id " +
                    "WHERE t.user_id = ? ORDER BY t.date, t.start_time");
                stmt.setInt(1, userId);
                ResultSet trs = stmt.executeQuery();
                while (trs.next()) {
                    records.add(new TimetableRecord(
                        trs.getString(1), trs.getString(2), trs.getString(3), trs.getString(4), trs.getString(5), trs.getBoolean(6)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    // Example: Generate a 7-day timetable, languages (e.g. Espanol) every day, others distributed
    private static boolean generateTimetable(String username) {
        try (FileInputStream fis = new FileInputStream("dbconfig.properties");
             FileInputStream settingsFis = new FileInputStream("settings.properties")) {
            Properties props = new Properties();
            props.load(fis);
            Properties settings = new Properties();
            settings.load(settingsFis);
            String url = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port") + "/" + props.getProperty("database");
            String dbUser = props.getProperty("username");
            String dbPass = props.getProperty("password");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rs = getUser.executeQuery();
            if (!rs.next()) return false;
            int userId = rs.getInt(1);
            // Get subjects and courses
            List<Integer> langSubjectIds = new ArrayList<>();
            List<Integer> otherSubjectIds = new ArrayList<>();
            Map<Integer, String> subjectNames = new HashMap<>();
            PreparedStatement subjStmt = conn.prepareStatement("SELECT id, subject_name FROM subjects WHERE user_id = ?");
            subjStmt.setInt(1, userId);
            ResultSet srs = subjStmt.executeQuery();
            while (srs.next()) {
                int sid = srs.getInt(1);
                String sname = srs.getString(2).toLowerCase();
                subjectNames.put(sid, srs.getString(2));
                if (sname.contains("espanol") || sname.contains("spanish") || sname.contains("language")) {
                    langSubjectIds.add(sid);
                } else {
                    otherSubjectIds.add(sid);
                }
            }
            // Get courses
            List<Integer> courseIds = new ArrayList<>();
            PreparedStatement courseStmt = conn.prepareStatement("SELECT id FROM courses WHERE user_id = ?");
            courseStmt.setInt(1, userId);
            ResultSet crs = courseStmt.executeQuery();
            while (crs.next()) courseIds.add(crs.getInt(1));
            // Get user free time and semester/season
            LocalTime freeStart = LocalTime.parse(settings.getProperty("free_start", "18:00"));
            LocalTime freeEnd = LocalTime.parse(settings.getProperty("free_end", "22:00"));
            LocalDate semesterStart = LocalDate.parse(settings.getProperty("semester_start", LocalDate.now().toString()));
            LocalDate semesterEnd = LocalDate.parse(settings.getProperty("semester_end", LocalDate.now().plusMonths(6).toString()));
            int studyMinutes = (int) java.time.Duration.between(freeStart, freeEnd).toMinutes();
            int slotMinutes = 60;
            // Generate for 7 days within semester/season
            LocalDate today = LocalDate.now();
            for (int d = 0; d < 7; d++) {
                LocalDate date = today.plusDays(d);
                if (date.isBefore(semesterStart) || date.isAfter(semesterEnd)) continue;
                // Always add language subject(s) every day
                for (int sid : langSubjectIds) {
                    LocalTime st = freeStart;
                    LocalTime et = st.plusMinutes(slotMinutes);
                    PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO timetable (user_id, subject_id, course_id, date, start_time, stop_time, completed) VALUES (?, ?, NULL, ?, ?, ?, FALSE)");
                    ins.setInt(1, userId);
                    ins.setInt(2, sid);
                    ins.setDate(3, Date.valueOf(date));
                    ins.setTime(4, Time.valueOf(st));
                    ins.setTime(5, Time.valueOf(et));
                    ins.executeUpdate();
                }
                // Distribute other subjects
                int slots = (studyMinutes / slotMinutes) - langSubjectIds.size();
                int subjIdx = 0;
                for (int s = 0; s < slots && subjIdx < otherSubjectIds.size(); s++, subjIdx++) {
                    int sid = otherSubjectIds.get(subjIdx % otherSubjectIds.size());
                    LocalTime st = freeStart.plusMinutes((langSubjectIds.size() + s) * slotMinutes);
                    LocalTime et = st.plusMinutes(slotMinutes);
                    PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO timetable (user_id, subject_id, course_id, date, start_time, stop_time, completed) VALUES (?, ?, NULL, ?, ?, ?, FALSE)");
                    ins.setInt(1, userId);
                    ins.setInt(2, sid);
                    ins.setDate(3, Date.valueOf(date));
                    ins.setTime(4, Time.valueOf(st));
                    ins.setTime(5, Time.valueOf(et));
                    ins.executeUpdate();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void markCompleted(String username, TimetableRecord rec) {
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
                PreparedStatement upd = conn.prepareStatement(
                    "UPDATE timetable SET completed = TRUE WHERE user_id = ? AND date = ? AND start_time = ? AND subject_id = (SELECT id FROM subjects WHERE subject_name = ? AND user_id = ?)");
                upd.setInt(1, userId);
                upd.setString(2, rec.getDate());
                upd.setString(3, rec.getStartTime());
                upd.setString(4, rec.getSubjectName());
                upd.setInt(5, userId);
                upd.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
