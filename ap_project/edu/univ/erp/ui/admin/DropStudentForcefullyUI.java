package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class DropStudentForcefullyUI extends JPanel {

    private AdminService adminService = new AdminService();

    public DropStudentForcefullyUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Drop Student From Section (Force)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField secField = new JTextField();
        JTextField stuField = new JTextField();

        form.add(new JLabel("Section ID:"));
        form.add(secField);

        form.add(new JLabel("Student ID:"));
        form.add(stuField);

        JButton drop = new JButton("Drop");
        JButton back = new JButton("Back");

        JPanel btns = new JPanel();
        btns.add(drop);
        btns.add(back);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        drop.addActionListener(e -> {
            try {
                int sid = Integer.parseInt(secField.getText().trim());
                int stid = Integer.parseInt(stuField.getText().trim());

                if (adminService.dropStudentForcefully(sid, stid)) {
                    JOptionPane.showMessageDialog(this, "Student Removed!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Remove.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());
    }
}
