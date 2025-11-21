package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddStudentUI extends JPanel {

    private AdminService adminService = new AdminService();

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
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public AddStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Soft background

        // ---------- Header ----------
        JLabel title = new JLabel("Add Student", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Form Panel ----------
        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(5, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField rollField = new JTextField();
        JTextField programField = new JTextField();
        JTextField yearField = new JTextField();

        // Add labels and fields
        form.add(new JLabel("Username:")).setFont(labelFont);
        form.add(usernameField);

        form.add(new JLabel("Password:")).setFont(labelFont);
        form.add(passwordField);

        form.add(new JLabel("Roll No:")).setFont(labelFont);
        form.add(rollField);

        form.add(new JLabel("Program:")).setFont(labelFont);
        form.add(programField);

        form.add(new JLabel("Year:")).setFont(labelFont);
        form.add(yearField);

        add(form, BorderLayout.CENTER);

        // ---------- Buttons ----------
        JButton addBtn = new JButton("Add");
        JButton back = new JButton("Back");

        styleButton(addBtn, new Color(46, 204, 113), new Color(39, 174, 96)); // Green
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185)); // Blue

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
                String roll = rollField.getText().trim();
                String prog = programField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());

                Student s = new Student();
                s.setRollNo(roll);
                s.setProgram(prog);
                s.setYear(year);

                if (adminService.addStudent(s, username, password)) {
                    JOptionPane.showMessageDialog(this, "Student Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Student.");
                }

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
}
