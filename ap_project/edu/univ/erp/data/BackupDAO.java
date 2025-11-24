package edu.univ.erp.data;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupDAO {

    private final String AUTH_DB_URL = "jdbc:mysql://localhost:3306/auth_db";
    private final String ERP_DB_URL = "jdbc:mysql://localhost:3306/erp_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "qwerty";

    private final String BACKUP_FOLDER = "backups/";

    public BackupDAO() {
        // Ensure backup folder exists
        File folder = new File(BACKUP_FOLDER);
        if (!folder.exists()) folder.mkdirs();
    }

    // ------------------- BACKUP -------------------
    public boolean backupAll() {
        try (Connection authConn = DriverManager.getConnection(AUTH_DB_URL, DB_USER, DB_PASS);
             Connection erpConn = DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASS)) {

            backupTable(authConn, "users_auth", "auth_users.csv");
            backupTable(erpConn, "students", "students.csv");
            backupTable(erpConn, "instructors", "instructors.csv");
            backupTable(erpConn, "courses", "courses.csv");
            backupTable(erpConn, "sections", "sections.csv");
            backupTable(erpConn, "enrollments", "enrollments.csv");
            backupTable(erpConn, "grades", "grades.csv");
            backupSettings(erpConn);

            // Save last backup time
            saveLastBackupTime(erpConn);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void backupTable(Connection conn, String tableName, String fileName) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        try (PrintWriter writer = new PrintWriter(new File(BACKUP_FOLDER + fileName))) {
            // Write header
            for (int i = 1; i <= colCount; i++) {
                writer.print(meta.getColumnName(i));
                if (i < colCount) writer.print(",");
            }
            writer.println();

            // Write rows
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    writer.print(rs.getString(i) == null ? "" : rs.getString(i));
                    if (i < colCount) writer.print(",");
                }
                writer.println();
            }
        }
    }

    private void backupSettings(Connection conn) throws SQLException, IOException {
        backupTable(conn, "settings", "settings.csv");
    }

    private void saveLastBackupTime(Connection conn) throws SQLException {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO settings (key_name, value) VALUES ('last_backup', ?) " +
                        "ON DUPLICATE KEY UPDATE value = ?")) {
            ps.setString(1, now);
            ps.setString(2, now);
            ps.executeUpdate();
        }
    }

    public String getLastBackupTime() {
        try (Connection conn = DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT value FROM settings WHERE key_name='last_backup'")) {

            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Never";
    }

    // ------------------- RESTORE -------------------
    public boolean restoreAll() {
        try (Connection authConn = DriverManager.getConnection(AUTH_DB_URL, DB_USER, DB_PASS);
             Connection erpConn = DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASS)) {

            // Disable FK checks for restore
            erpConn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");
            authConn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");

            restoreTable(authConn, "users_auth", "auth_users.csv");
            restoreTable(erpConn, "students", "students.csv");
            restoreTable(erpConn, "instructors", "instructors.csv");
            restoreTable(erpConn, "courses", "courses.csv");
            restoreTable(erpConn, "sections", "sections.csv");
            restoreTable(erpConn, "enrollments", "enrollments.csv");
            restoreTable(erpConn, "grades", "grades.csv");
            restoreTable(erpConn, "settings", "settings.csv");

            erpConn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1");
            authConn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void restoreTable(Connection conn, String tableName, String fileName) throws SQLException, IOException {
        File file = new File(BACKUP_FOLDER + fileName);
        if (!file.exists()) return; // Skip if backup not found

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine(); // Skip header

            conn.createStatement().execute("DELETE FROM " + tableName); // clear table

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1);
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    placeholders.append("?");
                    if (i < values.length - 1) placeholders.append(",");
                }

                String sql = "INSERT INTO " + tableName + " VALUES(" + placeholders + ")";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < values.length; i++) {
                        ps.setString(i + 1, values[i].isEmpty() ? null : values[i]);
                    }
                    ps.executeUpdate();
                }
            }
        }
    }
}
