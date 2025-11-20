package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AssignInstructorUI extends JPanel {

    private AdminService adminService = new AdminService();

    public AssignInstructorUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Assign Instructor to Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField secField = new JTextField();
        JTextField instField = new JTextField();

        form.add(new JLabel("Section ID:"));
        form.add(secField);

        form.add(new JLabel("Instructor ID:"));
        form.add(instField);

        JButton assign = new JButton("Assign");
        JButton back = new JButton("Back");

        JPanel btns = new JPanel();
        btns.add(assign);
        btns.add(back);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        assign.addActionListener(e -> {
            try {
                int sid = Integer.parseInt(secField.getText().trim());
                int iid = Integer.parseInt(instField.getText().trim());

                if (adminService.setInstructor(sid, iid)) {
                    JOptionPane.showMessageDialog(this, "Instructor Assigned!");
                } else {
                    JOptionPane.showMessageDialog(this, "Assignment Failed.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
