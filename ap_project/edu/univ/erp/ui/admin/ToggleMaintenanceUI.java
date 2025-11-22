package edu.univ.erp.ui.admin;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ToggleMaintenanceUI extends JPanel {

    private AdminService adminService = new AdminService();

    public ToggleMaintenanceUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel banner = null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("Toggle Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        if(banner==null) {
            add(title, BorderLayout.NORTH);
        }
        else{
            add(title,BorderLayout.CENTER);
        }

        JButton toggle = new JButton("Toggle Maintenance");
        JButton back = new JButton("Back");

        JPanel center = new JPanel();
        center.add(toggle);
        center.add(back);

        add(center, BorderLayout.CENTER);

        toggle.addActionListener(e -> {
            try {
                adminService.toggleMaintenance();
                JOptionPane.showMessageDialog(this, "Maintenance Mode Updated!");
                mainFrame.addScreen("toggle_maintenance", new ToggleMaintenanceUI(mainFrame));
                mainFrame.showScreen("toggle_maintenance");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());
    }
}
