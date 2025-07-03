package daccounts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class DBConfig {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public DBConfig(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public boolean saveToFile(String filePath) {
        Properties props = new Properties();
        props.setProperty("host", host);
        props.setProperty("port", port);
        props.setProperty("database", database);
        props.setProperty("username", username);
        props.setProperty("password", password);
        try (FileWriter writer = new FileWriter(filePath)) {
            props.store(writer, "D-Accounts DB Configuration");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
