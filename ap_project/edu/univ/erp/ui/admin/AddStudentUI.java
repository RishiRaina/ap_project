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

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField uidField = new JTextField();
        JTextField rollField = new JTextField();
        JTextField programField = new JTextField();
        JTextField yearField = new JTextField();

        form.add(new JLabel("User ID:"));
        form.add(uidField);

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
                int userId = Integer.parseInt(uidField.getText().trim());
                String roll = rollField.getText().trim();
                String prog = programField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());

                Student s = new Student();
                s.setUserId(userId);
                s.setRollNo(roll);
                s.setProgram(prog);
                s.setYear(year);

                if (adminService.addStudent(s)) {
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
