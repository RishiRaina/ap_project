package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.access.MaintenanceChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToggleMaintenanceUI extends JPanel {

    private AdminService adminService = new AdminService();
    private JLabel statusLabel;

    // ---------- Rounded Panel Class ----------
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;

        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public ToggleMaintenanceUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // soft gray background

        // ---------- Header ----------
        JLabel title = new JLabel("Toggle Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Status Banner Panel ----------
        RoundedPanel statusPanel = new RoundedPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(new Color(236, 240, 241)); // light gray background

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.CENTER);

        updateStatusLabel(); // initialize status

        // ---------- Buttons Panel ----------
        RoundedPanel buttonPanel = new RoundedPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton toggle = new JButton("Toggle Maintenance");
        JButton back = new JButton("Back");

        styleButton(toggle, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        buttonPanel.add(toggle);
        buttonPanel.add(back);
        add(buttonPanel, BorderLayout.SOUTH);

        // ---------- Action Listeners ----------
        toggle.addActionListener(e -> {
            try {
                adminService.toggleMaintenance();
                updateStatusLabel();
                JOptionPane.showMessageDialog(this, "Maintenance Mode Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }

    // ---------- Button Styling ----------
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }

    // ---------- Update Status ----------
    private void updateStatusLabel() {
        boolean isOn = MaintenanceChecker.isMaintenanceOn();
        statusLabel.setText(isOn ? "Maintenance Mode: ON" : "Maintenance Mode: OFF");
        statusLabel.setBackground(isOn ? new Color(231, 76, 60) : new Color(46, 204, 113));
    }
}
