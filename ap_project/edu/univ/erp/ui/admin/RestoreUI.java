package edu.univ.erp.ui.admin;

import edu.univ.erp.data.BackupDAO;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class RestoreUI extends JPanel {

    private BackupDAO backupDAO = new BackupDAO();

    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;
        public RoundedPanel() { setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public RestoreUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));


        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Restore Database");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(2, 1, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

        JLabel infoLabel = new JLabel("<html>Warning: Restoring will replace the current database with the last backup.<br>Make sure you have a recent backup before proceeding.</html>");
        infoLabel.setFont(labelFont);
        infoLabel.setForeground(new Color(192, 57, 43));

        JButton restoreBtn = new JButton("Restore Now");
        styleButton(restoreBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        form.add(infoLabel);
        form.add(restoreBtn);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);


        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        buttons.add(backBtn);
        add(buttons, BorderLayout.SOUTH);


        restoreBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to restore the database?", "Confirm Restore", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = backupDAO.restoreAll();
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Database restored successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Restore failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backBtn.addActionListener(e -> mainFrame.refreshAdminDashboard());
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
