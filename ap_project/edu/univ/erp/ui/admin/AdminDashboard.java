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

        // ---------- HEADER ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);

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
        header.add(logoutBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---------- SIDEBAR ----------
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // ⬇⬇⬇ UPDATED BUTTON LIST — Added "Change Password"
        String[] buttonNames = {
                "Add Course", "Update Course", "Delete Course",
                "Add Section", "Assign Instructor",
                "Add Student", "Add Instructor",
                "Enroll Student", "Unenroll Student",
                "View All Students", "View All Instructors",
                "Toggle Maintenance Mode",
                "Change Password"    // NEW
        };

        for (String name : buttonNames) {
            JButton btn = createSidebarButton(name);
            sidebar.add(btn);

            // ACTIONS
            btn.addActionListener(e -> handleAction(name));
        }

        add(sidebar, BorderLayout.WEST);

        // ---------- MAIN CONTENT ----------
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(new Color(245, 245, 245));
        add(mainContent, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(52, 73, 94));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
            case "Change Password":            // ⬅⬅⬅ NEW OPTION
                openContent(new ChangePasswordUI());
                break;
        }
    }

    private void openContent(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}
