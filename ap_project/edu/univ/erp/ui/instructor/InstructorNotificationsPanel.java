package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Notification;
import edu.univ.erp.service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class InstructorNotificationsPanel extends JPanel {

    private final NotificationService notificationService = new NotificationService();
    private DefaultTableModel model;

    public InstructorNotificationsPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("My Notifications", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Time", "Title", "Message"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> loadNotifications());

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(refreshBtn);

        add(bottom, BorderLayout.SOUTH);

        loadNotifications();
    }

    private void loadNotifications() {
        model.setRowCount(0);

        if (!SessionManager.isLoggedIn()) return;

        List<Notification> list = notificationService.getMyNotifications();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Notification n : list) {
            String time = (n.getCreatedAt() != null) ? df.format(n.getCreatedAt()) : "";
            model.addRow(new Object[]{time, n.getTitle(), n.getMessage()});
        }
    }
}
