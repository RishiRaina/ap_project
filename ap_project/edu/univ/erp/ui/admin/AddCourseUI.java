package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AddCourseUI extends JPanel {

    private AdminService adminService = new AdminService();

    public AddCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Add New Course", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField codeField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField creditsField = new JTextField();

        form.add(new JLabel("Course Code:"));
        form.add(codeField);

        form.add(new JLabel("Title:"));
        form.add(titleField);

        form.add(new JLabel("Credits:"));
        form.add(creditsField);

        JButton submit = new JButton("Add Course");
        JButton back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(submit);
        buttons.add(back);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        submit.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String ctitle = titleField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());

                Course c = new Course();
                c.setCode(code);
                c.setTitle(ctitle);
                c.setCredits(credits);

                if (adminService.addCourse(c)) {
                    JOptionPane.showMessageDialog(this, "Course Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Course.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
