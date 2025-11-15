package edu.univ.erp.auth;

import java.sql.*;
import java.time.LocalDateTime;
import edu.univ.erp.data.AuthDatabaseConnection;

/**
 * AuthHelper — Handles login, password change, logout for ERP system.
 * Uses bcrypt for secure password hashing.
 * Maintains clean separation of auth DB access and session management.
 */

public final class AuthHelper {

    private AuthHelper() {
        // Prevent instantiation of helper class
    }

    /**
     * Result wrapper for login attempts
     */
    public static class AuthResult {
        public final boolean success;
        public final int userId;
        public final String role;
        public final String message;

        public AuthResult(boolean success, int userId, String role, String message) {
            this.success = success;
            this.userId = userId;
            this.role = role;
            this.message = message;
        }
    }

    /**
     * Attempt user login given username & plaintext password.
     * Returns AuthResult indicating success/failure and user details.
     */
    public static AuthResult login(String username, String password) {
        final String sql = "SELECT user_id, role, password_hash, status FROM users_auth WHERE username = ?";

        try (Connection con = AuthDatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return failureResult("Incorrect username or password.");
                }

                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                String storedHash = rs.getString("password_hash");
                String status = rs.getString("status");

                if (!"ACTIVE".equalsIgnoreCase(status)) {
                    return failureResult("Account inactive or locked.");
                }

                // Verify password with bcrypt
                if (!PasswordHash.verify(password, storedHash)) {
                    return failureResult("Incorrect username or password.");
                }

                // Update last login timestamp
                updateLastLogin(con, userId);

                // Create user session
                SessionManager.setCurrentUser(userId, role);

                return new AuthResult(true, userId, role, "Login successful.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return failureResult("Database error during login.");
        }
    }

    /**
     * Change user's password, given old password for verification.
     * Returns true if password changed successfully, false otherwise.
     */
    public static boolean changePassword(int userId, String oldPassword, String newPassword) {
        final String selectSql = "SELECT password_hash FROM users_auth WHERE user_id = ?";
        final String updateSql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

        try (Connection con = AuthDatabaseConnection.getConnection();
             PreparedStatement selectPs = con.prepareStatement(selectSql)) {

            selectPs.setInt(1, userId);
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    System.err.println("User ID not found during password change");
                    return false;
                }

                String storedHash = rs.getString("password_hash");
                if (!PasswordHash.verify(oldPassword, storedHash)) {
                    System.err.println("Old password mismatch");
                    return false;
                }

                String newHash = PasswordHash.hash(newPassword);

                try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                    updatePs.setString(1, newHash);
                    updatePs.setInt(2, userId);
                    updatePs.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logs out current user by clearing session.
     */
    public static void logout() {
        SessionManager.clear();
    }

    // ================= Private Helpers =================

    /**
     * Returns a standard failure AuthResult with generic message.
     */
    private static AuthResult failureResult(String message) {
        return new AuthResult(false, -1, null, message);
    }

    /**
     * Updates last_login field for the user.
     */
    private static void updateLastLogin(Connection conn, int userId) throws SQLException {
        final String updatelastloginquery = "UPDATE users_auth SET last_login = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updatelastloginquery)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

}
