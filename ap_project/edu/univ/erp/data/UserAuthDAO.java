package edu.univ.erp.data;

import edu.univ.erp.domain.UserAuth;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserAuthDAO {

    public boolean addUser(UserAuth user) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status, last_login) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getStatus());

            if (user.getLastLogin() != null)
                ps.setTimestamp(5, Timestamp.valueOf(user.getLastLogin()));
            else
                ps.setNull(5, Types.TIMESTAMP);

            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching userId for username " + username + ": " + e.getMessage());
        }

        return -1;
    }

    public String getUsernameByUserId(int userId) {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching username for userId " + userId + ": " + e.getMessage());
        }

        return null;
    }


    public UserAuth getUserByUsername(String username) {
        String sql = "SELECT * FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new UserAuth(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("password_hash"),
                    rs.getString("status"),
                    rs.getTimestamp("last_login") != null
                            ? rs.getTimestamp("last_login").toLocalDateTime()
                            : null
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
        }

        return null;
    }


    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = ? WHERE user_id = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        }
    }


    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users_auth SET status = ? WHERE user_id = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating status: " + e.getMessage());
            return false;
        }
    }


    public List<UserAuth> getAllUsers() {
        List<UserAuth> users = new ArrayList<>();
        String sql = "SELECT * FROM users_auth";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new UserAuth(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("password_hash"),
                    rs.getString("status"),
                    rs.getTimestamp("last_login") != null
                            ? rs.getTimestamp("last_login").toLocalDateTime()
                            : null
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }

        return users;
    }


    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users_auth WHERE user_id = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection conn = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching username for userId " + userId + ": " + e.getMessage());
        }

        return null;
    }

}
