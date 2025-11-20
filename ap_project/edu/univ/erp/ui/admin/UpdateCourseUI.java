package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class UpdateCourseUI extends JPanel {

    private AdminService adminService = new AdminService();

    public UpdateCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Update Course", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField courseIdField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField creditsField = new JTextField();

        form.add(new JLabel("Course ID:"));
        form.add(courseIdField);

        form.add(new JLabel("Updated Title:"));
        form.add(titleField);

        form.add(new JLabel("Updated Credits:"));
        form.add(creditsField);

        JButton submit = new JButton("Update");
        JButton back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(submit);
        buttons.add(back);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        submit.addActionListener(e -> {
            try {
                int id = Integer.parseInt(courseIdField.getText().trim());
                String text = titleField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());

                Course c = new Course();
                c.setCourseId(id);
                c.setTitle(text);
                c.setCredits(credits);

                if (adminService.updateCourse(c)) {
                    JOptionPane.showMessageDialog(this, "Course Updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update Failed!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
