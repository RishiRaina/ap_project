package edu.univ.erp.auth;

import edu.univ.erp.data.UserAuthDAO;

public class SessionManager {

    private static int currentUserId = -1;
    private static String currentUserRole = null;

    public static void setCurrentUser(int userId, String role) {
        currentUserId = userId;
        currentUserRole = role;
    }
    private static UserAuthDAO userAuthDAO = new UserAuthDAO();

    public static String getUsernameByUserId(int userId) {
        return userAuthDAO.getUsernameById(userId);
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static boolean isLoggedIn() {
        return currentUserId != -1;
    }

    public static void clear() {
        currentUserId = -1;
        currentUserRole = null;
    }
}
