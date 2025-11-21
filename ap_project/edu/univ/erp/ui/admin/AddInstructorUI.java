package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AddInstructorUI extends JPanel {

    private AdminService adminService = new AdminService();

    public AddInstructorUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Add Instructor", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        // Form with username, password, and department
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField deptField = new JTextField();

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel("Department:"));
        form.add(deptField);

        JButton addBtn = new JButton("Add");
        JButton back = new JButton("Back");

        JPanel btns = new JPanel();
        btns.add(addBtn);
        btns.add(back);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String dept = deptField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || dept.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                // Call AdminService to add instructor using username/password/department
                if (adminService.addInstructor(username, password, dept)) {
                    JOptionPane.showMessageDialog(this, "Instructor Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add instructor. Username may already exist.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
