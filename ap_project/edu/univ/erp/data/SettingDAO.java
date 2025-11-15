package edu.univ.erp.data;

import edu.univ.erp.domain.Setting;
import java.sql.*;


public class SettingDAO {

    public boolean addSetting(Setting s) {
        String sql = "INSERT INTO settings(`key_name`, `value`) VALUES (?, ?)";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getKey());
            ps.setString(2, s.getValue());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding setting: " + e.getMessage());
            return false;
        }
    }

    public String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE `key_name` = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching setting: " + e.getMessage());
        }
        return null;
    }

    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE settings SET value = ? WHERE `key_name` = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, key);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating setting: " + e.getMessage());
            return false;
        }
    }
}
