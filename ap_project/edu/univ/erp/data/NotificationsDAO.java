package edu.univ.erp.data;

import edu.univ.erp.domain.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {

    public boolean insertNotification(Notification n) {
        final String sql = "INSERT INTO notifications " +
                "(target_user_id, target_role, title, message, created_at) " +
                "VALUES (?, ?, ?, ?, NOW())";

        try (Connection con = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // userId
            if (n.getTargetUserId() == null)
                ps.setNull(1, Types.INTEGER);
            else
                ps.setInt(1, n.getTargetUserId());

            // role
            if (n.getTargetRole() == null)
                ps.setNull(2, Types.VARCHAR);
            else
                ps.setString(2, n.getTargetRole());

            ps.setString(3, n.getTitle());
            ps.setString(4, n.getMessage());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // IMPORTANT: this is what your GradeEntryDialog is calling
    public void addNotification(Notification n) {
        insertNotification(n);
    }

    public List<Notification> getNotificationsForUser(int userId, String role) {

        final String sql =
                "SELECT id, target_user_id, target_role, title, message, created_at " +
                        "FROM notifications " +
                        "WHERE target_user_id = ? " +
                        "   OR (target_user_id IS NULL AND (target_role = ? OR target_role = 'ALL')) " +
                        "ORDER BY created_at DESC";

        List<Notification> list = new ArrayList<>();

        try (Connection con = ERPDatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));

                    int user = rs.getInt("target_user_id");
                    if (rs.wasNull()) n.setTargetUserId(null);
                    else n.setTargetUserId(user);

                    n.setTargetRole(rs.getString("target_role"));
                    n.setTitle(rs.getString("title"));
                    n.setMessage(rs.getString("message"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));

                    list.add(n);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
