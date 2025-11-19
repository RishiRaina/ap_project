package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;

    public InstructorDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // role check
        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // maintenance banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        // title
        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        //button panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));
        panel.setBackground(Color.WHITE);

        JButton mysecBtn = createButton("My Sections");
        JButton statsBtn = createButton("View Class Stats");
        JButton exportBtn = createButton("Export Grades CSV");
        JButton logoutBtn = createButton("Logout");

        panel.add(mysecBtn);
        panel.add(statsBtn);
        panel.add(exportBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        // buttons for new screens

        mysecBtn.addActionListener(e -> {
            mainFrame.addScreen("instructor_my_sections", new InstructorSections(mainFrame));
            mainFrame.showScreen("instructor_my_sections");
        });

        statsBtn.addActionListener(e -> {
            mainFrame.addScreen("instructor_stats_select_section", new InstructorStatsSectionSelect(mainFrame));
            mainFrame.showScreen("instructor_stats_select_section");
        });

        exportBtn.addActionListener(e -> {
            mainFrame.addScreen("instructor_export_csv", new ExportGrades(mainFrame));
            mainFrame.showScreen("instructor_export_csv");
        });

        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });
    }

    // Reusable button builder for consistent UI
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 20));
        return btn;
    }
}
