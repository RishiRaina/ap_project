package edu.univ.erp.data;

import edu.univ.erp.domain.Admin;
import java.sql.*;

public class AdminDAO {

    public boolean addAdmin(Admin a) {
        String sql = "INSERT INTO admins(admin_id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, a.getAdminId());
            ps.setString(2, a.getName());
            ps.setString(3, a.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
            return false;
        }
    }

    public Admin getAdminById(int id) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        try (Connection conn = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Admin(
                    rs.getInt("admin_id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching admin: " + e.getMessage());
        }
        return null;
    }
}
