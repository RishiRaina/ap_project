package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class DeleteCourseUI extends JPanel {

    private AdminService adminService = new AdminService();

    public DeleteCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Delete Course", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(1, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField codeField = new JTextField();

        form.add(new JLabel("Course Code:"));   // 🔥 changed from ID to code
        form.add(codeField);

        JButton delete = new JButton("Delete");
        JButton back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(delete);
        buttons.add(back);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        delete.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();

                if (adminService.deleteCourse(code)) {
                    JOptionPane.showMessageDialog(this, "Course Deleted Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Delete!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
