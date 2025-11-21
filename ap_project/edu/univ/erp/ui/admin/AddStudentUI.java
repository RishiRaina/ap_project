package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AddStudentUI extends JPanel {

    private AdminService adminService = new AdminService();

    public AddStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Add Student", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        // Updated to 5 rows: Username, Password, Roll No, Program, Year
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField(); // Masked input
        JTextField rollField = new JTextField();
        JTextField programField = new JTextField();
        JTextField yearField = new JTextField();

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel("Roll No:"));
        form.add(rollField);

        form.add(new JLabel("Program:"));
        form.add(programField);

        form.add(new JLabel("Year:"));
        form.add(yearField);

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
                String roll = rollField.getText().trim();
                String prog = programField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());

                Student s = new Student();
                s.setRollNo(roll);
                s.setProgram(prog);
                s.setYear(year);

                // Pass username and password to AdminService
                if (adminService.addStudent(s, username, password)) {
                    JOptionPane.showMessageDialog(this, "Student Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
