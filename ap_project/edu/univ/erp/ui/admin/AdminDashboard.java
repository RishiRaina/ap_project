package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JPanel {

    private MainFrame mainFrame;
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

        // Top buttons panel
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setOpaque(false);

        // Change Password button
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changePassBtn.setForeground(Color.WHITE);
        changePassBtn.setBackground(new Color(46, 204, 113));
        changePassBtn.setFocusPainted(false);
        changePassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePassBtn.addActionListener(e -> {
            mainFrame.addScreen("adminChangePassword", new ChangePasswordUI(mainFrame));
            mainFrame.showScreen("adminChangePassword");
        });

        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });

        topButtons.add(changePassBtn);
        topButtons.add(logoutBtn);
        header.add(topButtons, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===================== SIDEBAR =====================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        String[] menuItems = {
                "Add Course", "Update Course", "Delete Course",
                "Add Section", "Assign Instructor",
                "Add Student", "Add Instructor",
                "Enroll Student", "Unenroll Student",
                "View All Students", "View All Instructors",
                "Toggle Maintenance Mode"
        };

        for (String name : menuItems) {
            JButton btn = createSidebarButton(name);
            sidebar.add(btn);
            btn.addActionListener(e -> handleAction(name));
        }

        add(sidebar, BorderLayout.WEST);

        // ===================== CENTER CONTENT =====================
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(245, 245, 245));
        add(mainContent, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(52, 73, 94));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(52, 73, 94));
            }
        });
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
        }
    }

    public void openContent(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}
