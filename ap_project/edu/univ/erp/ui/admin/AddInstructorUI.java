package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Instructor;
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

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField uidField = new JTextField();
        JTextField deptField = new JTextField();

        form.add(new JLabel("User ID:"));
        form.add(uidField);

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
                int userId = Integer.parseInt(uidField.getText().trim());
                String dept = deptField.getText().trim();

                Instructor i = new Instructor();
                i.setUserId(userId);
                i.setDepartment(dept);

                if (adminService.addInstructor(i)) {
                    JOptionPane.showMessageDialog(this, "Instructor Added!");
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
