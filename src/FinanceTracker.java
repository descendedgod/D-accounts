package daccounts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.time.LocalDate;

public class FinanceTracker {
    public static void show(Stage primaryStage, String username) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Finance Tracker");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: #fff;");
        root.setStyle("-fx-background-color: #181c2f;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount received");
        Button splitBtn = new Button("Split & Save");
        Label result = new Label();
        result.setStyle("-fx-text-fill: #fff;");

        // Table for finance records
        TableView<FinanceRecord> table = new TableView<>();
        TableColumn<FinanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<FinanceRecord, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<FinanceRecord, Double> meCol = new TableColumn<>("For Me");
        meCol.setCellValueFactory(new PropertyValueFactory<>("forMe"));
        TableColumn<FinanceRecord, Double> momCol = new TableColumn<>("For Mom");
        momCol.setCellValueFactory(new PropertyValueFactory<>("forMom"));
        TableColumn<FinanceRecord, Double> grandmaCol = new TableColumn<>("For Grandma");
        grandmaCol.setCellValueFactory(new PropertyValueFactory<>("forGrandma"));
        table.getColumns().addAll(dateCol, amountCol, meCol, momCol, grandmaCol);
        table.setItems(getFinanceRecords(username));
        table.setPrefHeight(200);

        splitBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                double me = amount * 0.5;
                double mom = amount * 0.3;
                double grandma = amount * 0.2;
                boolean saved = saveFinance(username, amount, me, mom, grandma);
                if (saved) {
                    result.setText(String.format("Split: You: %.2f | Mom: %.2f | Grandma: %.2f (Saved)", me, mom, grandma));
                    table.setItems(getFinanceRecords(username));
                } else {
                    result.setText("Error saving to database.");
                }
            } catch (Exception ex) {
                result.setText("Invalid amount.");
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> Dashboard.show(primaryStage, username));

        root.getChildren().addAll(title, amountField, splitBtn, result, table, backBtn);
        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Finance Tracker");
        primaryStage.show();
    }

    public static class FinanceRecord {
        private String date;
        private double amount, forMe, forMom, forGrandma;
        public FinanceRecord(String date, double amount, double forMe, double forMom, double forGrandma) {
            this.date = date; this.amount = amount; this.forMe = forMe; this.forMom = forMom; this.forGrandma = forGrandma;
        }
        public String getDate() { return date; }
        public double getAmount() { return amount; }
        public double getForMe() { return forMe; }
        public double getForMom() { return forMom; }
        public double getForGrandma() { return forGrandma; }
    }

    private static ObservableList<FinanceRecord> getFinanceRecords(String username) {
        ObservableList<FinanceRecord> records = FXCollections.observableArrayList();
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
                PreparedStatement stmt = conn.prepareStatement("SELECT date_paid, amount, for_me, for_mom, for_grandma FROM finance WHERE user_id = ? ORDER BY date_paid DESC");
                stmt.setInt(1, userId);
                ResultSet frs = stmt.executeQuery();
                while (frs.next()) {
                    records.add(new FinanceRecord(
                        frs.getString(1),
                        frs.getDouble(2),
                        frs.getDouble(3),
                        frs.getDouble(4),
                        frs.getDouble(5)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    private static boolean saveFinance(String username, double amount, double me, double mom, double grandma) {
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
                PreparedStatement insert = conn.prepareStatement("INSERT INTO finance (user_id, amount, date_paid, for_me, for_mom, for_grandma) VALUES (?, ?, ?, ?, ?, ?)");
                insert.setInt(1, userId);
                insert.setDouble(2, amount);
                insert.setDate(3, Date.valueOf(LocalDate.now()));
                insert.setDouble(4, me);
                insert.setDouble(5, mom);
                insert.setDouble(6, grandma);
                insert.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
