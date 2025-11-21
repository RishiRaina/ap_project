package edu.univ.erp.ui.admin;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AddSectionUI extends JPanel {

    private SectionService sectionService = new SectionService();
    private UserAuthDAO UserAuthDAO = new UserAuthDAO();

    public AddSectionUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Add Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        // UI Fields
        JTextField courseCodeField = new JTextField();
        JTextField instrUserField = new JTextField();   // optional
        JTextField timeField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField deadlineField = new JTextField();    // YYYY-MM-DD

        // Add rows
        form.add(new JLabel("Course Code:"));
        form.add(courseCodeField);

        form.add(new JLabel("Instructor Username (optional):"));
        form.add(instrUserField);

        form.add(new JLabel("Day & Time:"));
        form.add(timeField);

        form.add(new JLabel("Room:"));
        form.add(roomField);

        form.add(new JLabel("Capacity:"));
        form.add(capacityField);

        form.add(new JLabel("Semester:"));
        form.add(semesterField);

        form.add(new JLabel("Year:"));
        form.add(yearField);

        form.add(new JLabel("Registration Deadline (YYYY-MM-DD):"));
        form.add(deadlineField);

        JButton addBtn = new JButton("Add Section");
        JButton back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(back);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // Add Section button logic
        addBtn.addActionListener(e -> {
            try {
                String courseCode = courseCodeField.getText().trim();
                String instrUsername = instrUserField.getText().trim();
                String dayTime = timeField.getText().trim();
                String room = roomField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String semester = semesterField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                String deadlineStr = deadlineField.getText().trim();

                // Convert deadline
                java.sql.Date deadline = java.sql.Date.valueOf(deadlineStr);

                // Convert instructor username → instructor_id
                Integer instructorId = null;
                if (!instrUsername.isEmpty()) {
                    instructorId = UserAuthDAO.getUserByUsername(instrUsername).getUserId();

                    if (instructorId == null) {
                        JOptionPane.showMessageDialog(this, "Instructor not found!");
                        return;
                    }
                }

                // Create Section object
                Section s = new Section(
                        0,          // section_id auto
                        0,          // course_id filled by DAO using courseCode
                        instructorId,
                        dayTime,
                        room,
                        capacity,
                        semester,
                        year,
                        deadline
                );

                boolean success = sectionService.addSection(s, courseCode);

                if (success) {
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
