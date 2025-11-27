package edu.univ.erp.service;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.NotificationsDAO;
import edu.univ.erp.domain.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationsDAO dao = new NotificationsDAO();


    public void notifyUser(int userId, String title, String message) {
        Notification n = new Notification(userId, null, title, message);
        dao.insertNotification(n);
    }


    public void notifyRole(String role, String title, String message) {
        Notification n = new Notification(null, role, title, message);
        dao.insertNotification(n);
    }


    public void notifyAllUsers(String title, String message) {
        Notification n = new Notification(null, "ALL", title, message);
        dao.insertNotification(n);
    }


    public List<Notification> getMyNotifications() {
        int userId = SessionManager.getCurrentUserId();
        String role = SessionManager.getCurrentUserRole();
        return dao.getNotificationsForUser(userId, role);
    }
}
