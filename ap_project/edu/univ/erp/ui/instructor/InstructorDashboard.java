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

    private final MainFrame mainFrame;
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

        // ===================== TOP BUTTONS =====================
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setOpaque(false);

        JButton changePassBtn = styledTopButton("Change Password", new Color(46, 204, 113));
        changePassBtn.addActionListener(e -> {
            mainFrame.addScreen("instructor_change_password", new InstructorChangePasswordUI(mainFrame));
            mainFrame.showScreen("instructor_change_password");
        });

        JButton logoutBtn = styledTopButton("Logout", new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });

        topButtons.add(changePassBtn);
        topButtons.add(logoutBtn);
        header.add(topButtons, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===================== SIDEBAR =====================
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        String[] menu = {
                "My Sections",
                "View Class Stats",
                "Download Grades (CSV)",
                "Notifications"
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

        setCenter(new InstructorSections(mainFrame));

        // ===================== MAINTENANCE BANNER =====================
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel warn = new JPanel(new BorderLayout());
            warn.setBackground(new Color(255, 179, 71));
            warn.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel msg = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            msg.setFont(new Font("Segoe UI", Font.BOLD, 16));

            warn.add(msg);
            add(warn, BorderLayout.SOUTH);
        }
    }

    private JButton sidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(52, 73, 94));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
            case "My Sections":
                setCenter(new InstructorSections(mainFrame));
                break;
            case "View Class Stats":
                setCenter(new InstructorStatsSectionSelect(mainFrame));
                break;
            case "Download Grades (CSV)":
                setCenter(new ExportGrades(mainFrame));
                break;

            case "Notifications":
                setCenter(new InstructorNotificationsPanel());
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
