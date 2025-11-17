package edu.univ.erp.ui.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewTimeTable extends JPanel {

    private MainFrame mainFrame;
    private EnrollmentDAO enrollmentDAO;
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;

    public ViewTimeTable(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.enrollmentDAO = new EnrollmentDAO();
        this.sectionDAO = new SectionDAO();
        this.courseDAO = new CourseDAO();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // TABLE: Day/Time, Room, Course Code/Title
        String[] cols = {"Day/Time", "Room", "Course Code", "Course Title"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // BACK BUTTON
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );

        // Load data
        loadTimetable(model);
    }

    private void loadTimetable(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section == null) continue;

            Course course = courseDAO.getCourseById(section.getCourseId());

            model.addRow(new Object[]{
                    section.getDayTime(),
                    section.getRoom(),
                    (course != null ? course.getCode() : "N/A"),
                    (course != null ? course.getTitle() : "N/A")
            });
        }
    }
}
