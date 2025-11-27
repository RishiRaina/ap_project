package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AuthDatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String USER = "root";
    private static final String PASSWORD = "nehal-011";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
