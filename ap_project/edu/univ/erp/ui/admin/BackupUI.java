package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class BackupUI extends JPanel {

    public BackupUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Backup Database", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        // Center panel
        JPanel center = new JPanel();
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));
        center.setLayout(new GridLayout(3, 1, 20, 20));

        JLabel info = new JLabel("Click the button below to create a backup of the database.", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton backupBtn = new JButton("Backup Now");
        styleButton(backupBtn, new Color(46, 204, 113), new Color(39, 174, 96));

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(231, 76, 60));

        backupBtn.addActionListener(e -> {
            // TODO: Implement actual backup logic here
            boolean success = true; // placeholder

            if (success) {
                statusLabel.setText("Database backup completed successfully!");
                statusLabel.setForeground(new Color(46, 204, 113));
            } else {
                statusLabel.setText("Database backup failed.");
                statusLabel.setForeground(new Color(231, 76, 60));
            }
        });

        center.add(info);
        center.add(backupBtn);
        center.add(statusLabel);

        add(center, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(normal); }
        });
    }
}
