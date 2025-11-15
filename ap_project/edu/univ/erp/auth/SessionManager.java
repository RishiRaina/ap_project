package edu.univ.erp.auth;

public class SessionManager {

    private static int currentUserId = -1;
    private static String currentUserRole = null;

    public static void setCurrentUser(int userId, String role) {
        currentUserId = userId;
        currentUserRole = role;
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
