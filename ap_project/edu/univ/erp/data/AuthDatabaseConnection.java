package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AuthDatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String USER = "root";
    private static final String PASSWORD = "yourpassword";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to Auth database successfully.");
            } catch (SQLException | ClassNotFoundException e) {
                System.err.println("Auth database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
