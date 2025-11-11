package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class MainDashboard extends JFrame {

    public MainDashboard(int userId, String userRole) {
        setTitle("University ERP Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        if (edu.univ.erp.util.AppState.isMaintenanceMode && ("student".equalsIgnoreCase(userRole) || "instructor".equalsIgnoreCase(userRole))) {
            JLabel banner = new JLabel("MAINTENANCE MODE: Application is view-only.", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            add(banner, BorderLayout.NORTH);
        }

        if ("student".equalsIgnoreCase(userRole)) {
            add(new StudentPanel(userId));
        } else if ("instructor".equalsIgnoreCase(userRole)) {
            add(new InstructorPanel(userId));
        } else if ("admin".equalsIgnoreCase(userRole)) {
            add(new AdminPanel(userId));
        } else {
            add(new JLabel("Welcome! Unknown role.", SwingConstants.CENTER));
        }
    }
}