package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {

    private MainFrame mainFrame;

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));
        btnPanel.setLayout(new GridLayout(0, 1, 12, 12));

        JButton addUserBtn = createButton("Add User");
        JButton createCourseBtn = createButton("Create Course");
        JButton editCourseBtn = createButton("Edit Course");
        JButton createSectionBtn = createButton("Create Section");
        JButton editSectionBtn = createButton("Edit Section");
        JButton assignBtn = createButton("Assign Instructor");
        JButton maintenanceBtn = createButton("Toggle Maintenance Mode");
        JButton backupBtn = createButton("Backup Database");
        JButton restoreBtn = createButton("Restore Database");
        JButton logoutBtn = createButton("Logout");

        btnPanel.add(addUserBtn);
        btnPanel.add(createCourseBtn);
        btnPanel.add(editCourseBtn);
        btnPanel.add(createSectionBtn);
        btnPanel.add(editSectionBtn);
        btnPanel.add(assignBtn);
        btnPanel.add(maintenanceBtn);
        btnPanel.add(backupBtn);
        btnPanel.add(restoreBtn);
        btnPanel.add(logoutBtn);

        add(btnPanel, BorderLayout.CENTER);

        // ACTIONS
        addUserBtn.addActionListener(e -> showNotReady("Add User"));
        createCourseBtn.addActionListener(e -> showNotReady("Create Course"));
        editCourseBtn.addActionListener(e -> showNotReady("Edit Course"));
        createSectionBtn.addActionListener(e -> showNotReady("Create Section"));
        editSectionBtn.addActionListener(e -> showNotReady("Edit Section"));
        assignBtn.addActionListener(e -> showNotReady("Assign Instructor"));
        maintenanceBtn.addActionListener(e -> showNotReady("Toggle Maintenance"));
        backupBtn.addActionListener(e -> showNotReady("Backup DB"));
        restoreBtn.addActionListener(e -> showNotReady("Restore DB"));

        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });
    }

    //helper to create buttons
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }


    //helper till other screens not linked or made
    private void showNotReady(String feature) {
        JOptionPane.showMessageDialog(
                this,
                feature + " screen is not built yet!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
