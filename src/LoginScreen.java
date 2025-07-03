package daccounts;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

public class LoginScreen {
    public static void show(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginBtn = new Button("Login");
        Label status = new Label();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passField, 1, 1);
        grid.add(loginBtn, 1, 2);
        grid.add(status, 1, 3);

        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            if (authenticate(username, password)) {
                status.setText("Login successful!");
                Dashboard.show(primaryStage, username);
            } else {
                status.setText("Invalid credentials.");
            }
        });

        Scene scene = new Scene(grid, 350, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("D-Accounts: Login");
        primaryStage.show();
    }

    private static boolean authenticate(String username, String password) {
        try (FileInputStream fis = new FileInputStream("dbconfig.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String url = "jdbc:mysql://" + props.getProperty("host") + ":" + props.getProperty("port") + "/" + props.getProperty("database");
            String dbUser = props.getProperty("username");
            String dbPass = props.getProperty("password");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString(1);
                // For demo: plain text check. Replace with hash check in production.
                return password.equals(hash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
