package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AddSectionUI extends JPanel {

    private AdminService adminService = new AdminService();

    public AddSectionUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        JLabel title = new JLabel("Add Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JTextField courseIdField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField capacityField = new JTextField();

        form.add(new JLabel("Course ID:"));
        form.add(courseIdField);

        form.add(new JLabel("Timings:"));
        form.add(timeField);

        form.add(new JLabel("Capacity:"));
        form.add(capacityField);

        JButton addBtn = new JButton("Add Section");
        JButton back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(back);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                int courseId = Integer.parseInt(courseIdField.getText().trim());
                String timing = timeField.getText().trim();
                int cap = Integer.parseInt(capacityField.getText().trim());

                Section s = new Section();
                s.setCourseId(courseId);
                s.setDayTime(timing);
                s.setCapacity(cap);

                if (adminService.addSection(s)) {
                    JOptionPane.showMessageDialog(this, "Section Added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Section.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
