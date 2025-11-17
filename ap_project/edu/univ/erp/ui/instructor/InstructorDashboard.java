package edu.univ.erp.ui.instructor;

import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;

    public InstructorDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        // ===== TITLE =====
        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));
        btnPanel.setLayout(new GridLayout(0, 1, 12, 12));

        JButton viewSectionsBtn = createButton("View My Sections");
        JButton enterScoresBtn = createButton("Enter Scores");
        JButton computeFinalBtn = createButton("Compute Final Grades");
        JButton statsBtn = createButton("Class Statistics");
        JButton exportCSVBtn = createButton("Export Grades (CSV)");
        JButton importCSVBtn = createButton("Import Grades (CSV)");
        JButton logoutBtn = createButton("Logout");

        btnPanel.add(viewSectionsBtn);
        btnPanel.add(enterScoresBtn);
        btnPanel.add(computeFinalBtn);
        btnPanel.add(statsBtn);
        btnPanel.add(exportCSVBtn);
        btnPanel.add(importCSVBtn);
        btnPanel.add(logoutBtn);

        add(btnPanel, BorderLayout.CENTER);

        // ========= BUTTON ACTIONS =========
        viewSectionsBtn.addActionListener(e -> showNotReady("View Sections"));
        enterScoresBtn.addActionListener(e -> showNotReady("Enter Scores"));
        computeFinalBtn.addActionListener(e -> showNotReady("Compute Final Grades"));
        statsBtn.addActionListener(e -> showNotReady("Class Stats"));
        exportCSVBtn.addActionListener(e -> showNotReady("Export CSV"));
        importCSVBtn.addActionListener(e -> showNotReady("Import CSV"));

        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });
    }

    //helper for buttons
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }

    // till other screens not ready
    private void showNotReady(String feature) {
        JOptionPane.showMessageDialog(
                this,
                feature + " screen is not built yet!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
