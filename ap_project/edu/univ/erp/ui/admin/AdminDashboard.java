package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JPanel {

    private final MainFrame mainFrame;
    private JPanel mainContent;

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ===================== HEADER =====================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // ===================== TOP BUTTONS =====================
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setOpaque(false);

        JButton changePassBtn = styledTopButton("Change Password", new Color(46, 204, 113));
        changePassBtn.addActionListener(e -> {
            mainFrame.addScreen("admin_change_password", new ChangePasswordUI(mainFrame));
            mainFrame.showScreen("admin_change_password");
        });

        JButton backupBtn = styledTopButton("Backup", new Color(241, 196, 15));
        backupBtn.addActionListener(e -> {
            mainFrame.addScreen("backup_ui", new BackupUI(mainFrame));
            mainFrame.showScreen("backup_ui");
        });

        JButton restoreBtn = styledTopButton("Restore", new Color(230, 126, 34));
        restoreBtn.addActionListener(e -> {
            mainFrame.addScreen("restore_ui", new RestoreUI(mainFrame));
            mainFrame.showScreen("restore_ui");
        });

        JButton logoutBtn = styledTopButton("Logout", new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });

        topButtons.add(changePassBtn);
        topButtons.add(backupBtn);
        topButtons.add(restoreBtn);
        topButtons.add(logoutBtn);

        header.add(topButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===================== SIDEBAR =====================
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        String[] menu = {
                "Add Course", "Update Course", "Delete Course",
                "Add Section", "Assign Instructor",
                "Add Student", "Add Instructor",
                "Enroll Student", "Unenroll Student",
                "View All Students", "View All Instructors",
                "Toggle Maintenance Mode", "Send Notification",
                "Upgrade Grade"  // <-- new button
        };

        for (String m : menu) {
            JButton btn = sidebarButton(m);
            btn.addActionListener(e -> handleAction(m));
            sidebar.add(btn);
        }

        add(sidebar, BorderLayout.WEST);

        // ===================== CENTER AREA =====================
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(245, 245, 245));
        add(mainContent, BorderLayout.CENTER);
    }

    private JButton sidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        b.setBackground(new Color(52, 73, 94));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(MouseEvent e) { b.setBackground(new Color(52, 73, 94)); }
        });

        return b;
    }

    private JButton styledTopButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return b;
    }

    private void handleAction(String action) {
        switch (action) {
            case "Add Course":
                openContent(new AddCourseUI(mainFrame));
                break;
            case "Update Course":
                openContent(new UpdateCourseUI(mainFrame));
                break;
            case "Delete Course":
                openContent(new DeleteCourseUI(mainFrame));
                break;
            case "Add Section":
                openContent(new AddSectionUI(mainFrame));
                break;
            case "Assign Instructor":
                openContent(new AssignInstructorUI(mainFrame));
                break;
            case "Add Student":
                openContent(new AddStudentUI(mainFrame));
                break;
            case "Add Instructor":
                openContent(new AddInstructorUI(mainFrame));
                break;
            case "Enroll Student":
                openContent(new EnrollStudentUI(mainFrame));
                break;
            case "Unenroll Student":
                openContent(new UnenrollStudentUI(mainFrame));
                break;
            case "View All Students":
                openContent(new ViewAllStudentsUI(mainFrame));
                break;
            case "View All Instructors":
                openContent(new ViewAllInstructorsUI(mainFrame));
                break;
            case "Toggle Maintenance Mode":
                openContent(new ToggleMaintenanceUI(mainFrame));
                break;
            case "Send Notification":
                openContent(new AdminNotificationBroadcastUI());
                break;
            case "Upgrade Grade":
                openContent(new UpgradeGradeUI(mainFrame)); // <-- new upgrade grade UI
                break;
        }
    }

    public void openContent(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}
