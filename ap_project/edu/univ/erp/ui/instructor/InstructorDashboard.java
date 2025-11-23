package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;
    private JPanel mainContent;

    public InstructorDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ===================== HEADER =====================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Instructor Dashboard");
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
            // Switch to Change Password UI
            mainFrame.addScreen("instructorChangePassword", new InstructorChangePasswordUI(mainFrame));
            mainFrame.showScreen("instructorChangePassword");
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
                "My Sections",
                "View Class Stats",
                "Download Grades (CSV)"
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

        // Default center view = My Sections
        setCenter(new InstructorSections(mainFrame));

        // ===================== MAINTENANCE BANNER =====================
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.SOUTH);
        }
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
            case "My Sections":
                setCenter(new InstructorSections(mainFrame));
                break;
            case "View Class Stats":
                setCenter(new InstructorStatsSectionSelect(mainFrame));
                break;
            case "Download Grades (CSV)":
                setCenter(new ExportGrades(mainFrame));
                break;
        }
    }

    // PUBLIC method for children to change the center content
    public void setCenter(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    // alias, just in case some old code calls this
    public void openContent(JPanel panel) {
        setCenter(panel);
    }
}
