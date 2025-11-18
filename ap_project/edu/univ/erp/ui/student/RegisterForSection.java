package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.StudentRegistrationService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterForSection extends JPanel {

    private MainFrame mainFrame;
    private SectionService sectionService;
    private CourseService courseService;
    private StudentRegistrationService regService;

    public RegisterForSection(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.sectionService = new SectionService();
        this.courseService = new CourseService();
        this.regService = new StudentRegistrationService();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Register for a Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Section ID", "Course Code", "Course Title", "Instructor ID", "Day/Time", "Room", "Capacity", "Deadline"};//table strcuture

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        //buttons
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(registerBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        //load the table
        loadSections(model);

        // register click handling
        registerBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a section first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int sectionId = (Integer) model.getValueAt(selectedRow, 0);
            int studentId = SessionManager.getCurrentUserId();

            try {
                regService.register(studentId, sectionId);
                JOptionPane.showMessageDialog(this, "Successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSections(model); // refresh

            } catch (AccessException ex2) {
                JOptionPane.showMessageDialog(this, ex2.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });


        // back
        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    // load section
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);

        List<Section> list = sectionService.getAllSections();

        for (Section s : list) {
            Course c = courseService.getCourseById(s.getCourseId());

            model.addRow(new Object[]{s.getSectionId(), (c != null ? c.getCode() : "N/A"), (c != null ? c.getTitle() : "N/A"), (s.getInstructorId() != null ? s.getInstructorId() : "TBA"), s.getDayTime(),
                    s.getRoom(), s.getCapacity(), s.getRegistrationDeadline()
            });
        }
    }
}
