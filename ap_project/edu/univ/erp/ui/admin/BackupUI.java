package edu.univ.erp.ui.admin;

import edu.univ.erp.data.BackupDAO;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class BackupUI extends JPanel {

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

    public BackupUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Backup Database");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ---------- FORM ----------
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(2, 1, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

        JLabel lastBackupLabel = new JLabel("Last Backup: " + backupDAO.getLastBackupTime());
        lastBackupLabel.setFont(labelFont);
        lastBackupLabel.setForeground(new Color(44, 62, 80));

        JButton backupBtn = new JButton("Backup Now");
        styleButton(backupBtn, new Color(241, 196, 15), new Color(243, 156, 18));

        form.add(lastBackupLabel);
        form.add(backupBtn);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);

        // ---------- BUTTONS ----------
        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        buttons.add(backBtn);
        add(buttons, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        backupBtn.addActionListener(e -> {
            boolean ok = backupDAO.backupAll();
            if (ok) {
                JOptionPane.showMessageDialog(this, "Backup completed successfully!");
                lastBackupLabel.setText("Last Backup: " + backupDAO.getLastBackupTime());
            } else {
                JOptionPane.showMessageDialog(this, "Backup failed!", "Error", JOptionPane.ERROR_MESSAGE);
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
