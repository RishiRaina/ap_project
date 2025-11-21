package edu.univ.erp.ui.admin;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignInstructorUI extends JPanel {

    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();

    public AssignInstructorUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Assign Instructor to Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        // -------------- Components ----------------

        JComboBox<Course> courseDropdown = new JComboBox<>();
        JComboBox<Section> sectionDropdown = new JComboBox<>();
        JComboBox<Integer> instructorDropdown = new JComboBox<>();

        // Load courses
        for (Course c : courseDAO.getAllCourses()) {
            courseDropdown.addItem(c);
        }

        // When course selected → load its sections
        courseDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems();

            Course selected = (Course) courseDropdown.getSelectedItem();
            if (selected == null) return;

            List<Section> secs = sectionDAO.getSectionsByCourse(selected.getCourseId());

            for (Section s : secs) sectionDropdown.addItem(s);
        });

        // Load instructors immediately
        for (Integer instId : instructorDAO.getAllInstructorIds()) {
            instructorDropdown.addItem(instId);
        }

        // Add components
        form.add(new JLabel("Select Course:"));
        form.add(courseDropdown);

        form.add(new JLabel("Select Section:"));
        form.add(sectionDropdown);

        form.add(new JLabel("Select Instructor:"));
        form.add(instructorDropdown);

        JButton assignBtn = new JButton("Assign");
        JButton backBtn = new JButton("Back");

        JPanel btnPanel = new JPanel();
        btnPanel.add(assignBtn);
        btnPanel.add(backBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // -------------- Assign Button Action ----------------

        assignBtn.addActionListener(e -> {
            Section selectedSec = (Section) sectionDropdown.getSelectedItem();
            Integer instId = (Integer) instructorDropdown.getSelectedItem();

            if (selectedSec == null) {
                JOptionPane.showMessageDialog(this, "No section selected.");
                return;
            }

            if (instId == null) {
                JOptionPane.showMessageDialog(this, "No instructor selected.");
                return;
            }

            boolean ok = sectionDAO.assignInstructor(selectedSec.getSectionId(), instId);

            if (ok)
                JOptionPane.showMessageDialog(this, "Instructor assigned successfully!");
            else
                JOptionPane.showMessageDialog(this, "Assignment failed.");
        });

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }
}
