package cz.cuni.mff.java.zapocet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/java_winter";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot establish database connection", ex);
            return null;
        }
    }
}
