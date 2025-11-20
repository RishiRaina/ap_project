package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ToggleMaintenanceUI extends JPanel {

    private AdminService adminService = new AdminService();

    public ToggleMaintenanceUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Toggle Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
