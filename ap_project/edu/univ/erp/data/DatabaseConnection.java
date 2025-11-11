package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/university_erp";
    private static final String USER = "root";
    private static final String PASSWORD = "yourpassword";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Database connected successfully.");
            } catch (SQLException | ClassNotFoundException e) {
                System.err.println(" Database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
