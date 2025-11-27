package edu.univ.erp.ui.admin;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddStudentUI extends JPanel {

    private AdminService adminService = new AdminService();

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

    public AddStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Add Student");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(6, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        form.setPreferredSize(new Dimension(600, 380));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        JTextField usernameField = new JTextField();
        usernameField.setFont(inputFont);
        addPlaceholder(usernameField, "Enter username...");

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(inputFont);
        addPlaceholder(passwordField, "Enter password...");

        JLabel rollLabel = new JLabel("Roll Number:");
        rollLabel.setFont(labelFont);
        JTextField rollField = new JTextField();
        rollField.setFont(inputFont);
        addPlaceholder(rollField, "Enter 7-digit roll number...");

        JLabel progLabel = new JLabel("Program:");
        progLabel.setFont(labelFont);
        JComboBox<String> programBox = new JComboBox<>();
        programBox.addItem("Select Program");
        programBox.addItem("CSAI");
        programBox.addItem("CSE");
        programBox.addItem("CSAM");
        programBox.addItem("CSD");
        programBox.addItem("CSB");
        programBox.addItem("CSS");
        programBox.addItem("ECE");
        programBox.addItem("EVE");
        programBox.addItem("CSEcon");
        programBox.setFont(inputFont);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(labelFont);
        JComboBox<String> yearBox = new JComboBox<>();
        yearBox.addItem("Select Year");
        for (int y = 2020; y <= 2030; y++) yearBox.addItem(String.valueOf(y));
        yearBox.setFont(inputFont);

        form.add(userLabel); form.add(usernameField);
        form.add(passLabel); form.add(passwordField);
        form.add(rollLabel); form.add(rollField);
        form.add(progLabel); form.add(programBox);
        form.add(yearLabel); form.add(yearBox);

        JButton addBtn = new JButton("Add Student");
        JButton back = new JButton("Back");

        styleButton(addBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));
        buttons.add(addBtn);
        buttons.add(back);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String roll = rollField.getText().replaceAll("\\s+", "");
                String program = (String) programBox.getSelectedItem();
                String yearStr = (String) yearBox.getSelectedItem();

                // ----- VALIDATION (unchanged, placeholder not counted as valid) -----

                if (username.isEmpty() || username.equals("Enter username...")) {
                    JOptionPane.showMessageDialog(this, "Username must not be empty.");
                    return;
                }

                if (!username.matches("[A-Za-z0-9]+")) {
                    JOptionPane.showMessageDialog(this, "Username must be alphanumeric only.");
                    return;
                }

                if (password.isEmpty() || password.equals("Enter password...")) {
                    JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                    return;
                }

                if (roll.isEmpty() || roll.equals("Enter 7-digit roll number...") || !roll.matches("\\d{7}")) {
                    JOptionPane.showMessageDialog(this, "Roll number must be a 7-digit number.");
                    return;
                }

                if (program.equals("Select Program")) {
                    JOptionPane.showMessageDialog(this, "Select a program.");
                    return;
                }

                if (yearStr.equals("Select Year")) {
                    JOptionPane.showMessageDialog(this, "Select a year.");
                    return;
                }

                int year = Integer.parseInt(yearStr);

                Student s = new Student();
                s.setRollNo(roll);
                s.setProgram(program);
                s.setYear(year);

                boolean success = adminService.addStudent(s, username, password);

                JOptionPane.showMessageDialog(this,
                        success ? "Student Added Successfully!" : "Failed to Add Student.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());
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
