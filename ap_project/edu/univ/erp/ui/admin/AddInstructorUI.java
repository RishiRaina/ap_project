package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddInstructorUI extends JPanel {

    private AdminService adminService = new AdminService();

    // ---------- Rounded Panel ----------
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public AddInstructorUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- Header ----------
        JLabel title = new JLabel("Add Instructor", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Form Panel ----------
        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

        // ---------- Text Fields ----------
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(120, 30)); // reduced width
        addPlaceholder(usernameField, "Enter username...");

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(120, 30)); // reduced width
        addPlaceholder(passwordField, "Enter password...");

        JTextField deptField = new JTextField();
        deptField.setPreferredSize(new Dimension(120, 30)); // reduced width
        addPlaceholder(deptField, "Enter department...");

        form.add(new JLabel("Username:")).setFont(labelFont);
        form.add(usernameField);
        form.add(new JLabel("Password:")).setFont(labelFont);
        form.add(passwordField);
        form.add(new JLabel("Department:")).setFont(labelFont);
        form.add(deptField);

        add(form, BorderLayout.CENTER);

        // ---------- Buttons ----------
        JButton addBtn = new JButton("Add");
        JButton back = new JButton("Back");
        styleButton(addBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.add(addBtn);
        btnPanel.add(back);
        add(btnPanel, BorderLayout.SOUTH);

        // ---------- Actions ----------
        addBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String dept = deptField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || dept.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                if (adminService.addInstructor(username, password, dept)) {
                    JOptionPane.showMessageDialog(this, "Instructor Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Instructor. Username may already exist.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());
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
            public void mouseEntered(MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(normal); }
        });
    }

    // ---------- Placeholder Helper ----------
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) { field.setText(""); field.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) { field.setForeground(Color.GRAY); field.setText(placeholder); }
            }
        });
    }
}
