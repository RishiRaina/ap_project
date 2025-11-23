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

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel title = new JLabel("Add Instructor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(520, 380));
        form.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(15, 20, 15, 20);
        fgbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 34));
        usernameField.setFont(fieldFont);
        addPlaceholder(usernameField, "Enter username...");

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 34));
        passwordField.setFont(fieldFont);
        addPlaceholder(passwordField, "Enter password...");

        String[] departments = {"CSB", "CSE", "ECE", "CSSS", "CSD", "CSAM"};
        JComboBox<String> deptDropdown = new JComboBox<>(departments);
        deptDropdown.setPreferredSize(new Dimension(200, 34));
        deptDropdown.setFont(fieldFont);

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        JLabel uLabel = new JLabel("Username:");
        uLabel.setFont(labelFont);
        form.add(uLabel, fgbc);

        fgbc.gridx = 1;
        form.add(usernameField, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 1;
        JLabel pLabel = new JLabel("Password:");
        pLabel.setFont(labelFont);
        form.add(pLabel, fgbc);

        fgbc.gridx = 1;
        form.add(passwordField, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 2;
        JLabel dLabel = new JLabel("Department:");
        dLabel.setFont(labelFont);
        form.add(dLabel, fgbc);

        fgbc.gridx = 1;
        form.add(deptDropdown, fgbc);

        JButton addBtn = new JButton("Add");
        JButton backBtn = new JButton("Back");
        styleButton(addBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(addBtn);
        btnPanel.add(backBtn);

        fgbc.gridx = 0;
        fgbc.gridy = 3;
        fgbc.gridwidth = 2;
        fgbc.anchor = GridBagConstraints.CENTER;
        form.add(btnPanel, fgbc);

        wrapper.add(form, gbc);
        add(wrapper, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String department = deptDropdown.getSelectedItem().toString();

                if (username.isEmpty() || username.equals("Enter username...")) {
                    JOptionPane.showMessageDialog(this, "Username cannot be empty.");
                    return;
                }

                if (!username.matches("[A-Za-z0-9 ]+")) {
                    JOptionPane.showMessageDialog(this,
                            "Username must be alphanumeric (letters, digits, spaces only).");
                    return;
                }

                if (password.isEmpty() || password.equals("Enter password...")) {
                    JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                    return;
                }

                if (password.length() < 5) {
                    JOptionPane.showMessageDialog(this, "Password must be at least 5 characters long.");
                    return;
                }

                if (adminService.addInstructor(username, password, department)) {
                    JOptionPane.showMessageDialog(this, "Instructor added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add instructor. Username may already exist.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
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

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(normal); }
        });
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}
