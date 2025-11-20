package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ChangeCapacityUI extends JPanel {

    private AdminService adminService = new AdminService();

    public ChangeCapacityUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Change Section Capacity", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField secField = new JTextField();
        JTextField capField = new JTextField();

        form.add(new JLabel("Section ID:"));
        form.add(secField);

        form.add(new JLabel("New Capacity:"));
        form.add(capField);

        JButton update = new JButton("Update");
        JButton back = new JButton("Back");

        JPanel btns = new JPanel();
        btns.add(update);
        btns.add(back);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        update.addActionListener(e -> {
            try {
                int sid = Integer.parseInt(secField.getText().trim());
                int newCap = Integer.parseInt(capField.getText().trim());

                if (adminService.changeCapacity(sid, newCap)) {
                    JOptionPane.showMessageDialog(this, "Capacity Updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Update.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}

