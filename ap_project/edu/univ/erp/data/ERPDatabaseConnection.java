package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ERPDatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/erp_db";
    private static final String USER = "root";
    private static final String PASSWORD = "qwerty";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("ERP database connection failed: " + e.getMessage());
            return null;
        }
    }
}
