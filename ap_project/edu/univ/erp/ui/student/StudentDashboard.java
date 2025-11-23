package edu.univ.erp.ui.student;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentDashboard extends JPanel {

    private final MainFrame mainFrame;
    private JPanel mainContent;

    public StudentDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ===================== HEADER =====================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Buttons panel on top-right
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setOpaque(false);

        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changePassBtn.setForeground(Color.WHITE);
        changePassBtn.setBackground(new Color(46, 204, 113));
        changePassBtn.setFocusPainted(false);
        changePassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePassBtn.addActionListener(e -> setCenter(new StudentChangePasswordUI(mainFrame)));

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
                "View Course Catalog",
                "My Enrollments",
                "Register for Section",
                "View Timetable",
                "View Grades",
                "Download Transcript (CSV)",
                "Download Transcript (PDF)"
        };

        for (String name : menuItems) {
            JButton btn = createSidebarButton(name);
            sidebar.add(btn);
            btn.addActionListener(e -> handleAction(name));
        }

        add(sidebar, BorderLayout.WEST);

        // ===================== CENTER AREA =====================
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(245, 245, 245));
        add(mainContent, BorderLayout.CENTER);

        // Default view: My Enrollments
        setCenter(new ViewMyEnrollments(mainFrame));

        // ===================== MAINTENANCE BANNER =====================
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner);
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

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(52, 73, 94));
            }
        });

        return b;
    }

    private void handleAction(String action) {
        switch (action) {
            case "View Course Catalog":
                setCenter(new ViewCourseCatalog(mainFrame));
                break;

            case "My Enrollments":
                setCenter(new ViewMyEnrollments(mainFrame));
                break;

            case "Register for Section":
                setCenter(new RegisterForSection(mainFrame));
                break;

            case "View Timetable":
                setCenter(new ViewTimeTable(mainFrame));
                break;

            case "View Grades":
                setCenter(new ViewGrades(mainFrame));
                break;

            case "Download Transcript (CSV)":
                setCenter(new DownloadTranscriptCSV(mainFrame));
                break;

            case "Download Transcript (PDF)":
                setCenter(new DownloadTranscriptPDF(mainFrame));
                break;
        }
    }

    public void setCenter(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}
